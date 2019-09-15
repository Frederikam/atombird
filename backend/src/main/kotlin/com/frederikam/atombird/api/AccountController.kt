package com.frederikam.atombird.api

import com.frederikam.atombird.data.Account
import com.frederikam.atombird.data.AccountRepository
import com.frederikam.atombird.data.Token
import com.frederikam.atombird.data.TokenRepository
import org.apache.commons.validator.routines.EmailValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory


@RestController
class AccountController(val accounts: AccountRepository, val tokens: TokenRepository) {

    private val log: Logger = LoggerFactory.getLogger(AccountController::class.java)

    val hashKeyLength = 256
    val hashIterations = 32
    val saltLength = 64
    val tokenLength = 128
    val random = SecureRandom()

    data class RegisterRequest(val email: String, val password: String)

    @PostMapping("/register")
    fun register(@RequestBody body: RegisterRequest): Mono<Token> {
        val salt = ByteArray(saltLength)
                .apply { random.nextBytes(this) }
                .run { Base64.getEncoder().encodeToString(this) }
        val hash = hashPassword(body.password, salt)
        val account = Account(body.email, salt, hash)
        account.new = true

        if(!EmailValidator.getInstance(false, false).isValid(body.email))
            throw InvalidEmailException()
        if (body.password.length < 8)
            throw PasswordTooShortException()

        return accounts.findById(body.email)
                .doOnNext { throw AccountAlreadyRegisteredException() }
                .then(accounts.save(account))
                .flatMap {
                    log.info("New user account: {}", it.email)
                    newTokenResponse(it)
                }
    }

    fun newTokenResponse(account: Account): Mono<Token> {
        val token = ByteArray(tokenLength)
                .apply { random.nextBytes(this) }
                .run { Base64.getEncoder().encodeToString(this) }
        return tokens.save(Token(token, account.email))
    }

    fun hashPassword(password: String, salt: String): String {
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val saltBin = Base64.getDecoder().decode(salt)
        val spec = PBEKeySpec(password.toCharArray(), saltBin, hashIterations, hashKeyLength)
        val key = skf.generateSecret(spec)
        return Base64.getEncoder().encodeToString(key.encoded)
    }

    class AccountAlreadyRegisteredException() :
            ResponseStatusException(HttpStatus.BAD_REQUEST, "This account is already registered")
    class InvalidEmailException() :
            ResponseStatusException(HttpStatus.BAD_REQUEST, "The given email is invalid.")
    class PasswordTooShortException() :
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must at least be 8 characters.")
}