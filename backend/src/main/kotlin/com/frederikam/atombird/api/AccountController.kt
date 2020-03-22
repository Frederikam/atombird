package com.frederikam.atombird.api

import com.frederikam.atombird.data.Account
import com.frederikam.atombird.data.AccountRepository
import com.frederikam.atombird.data.Token
import com.frederikam.atombird.data.TokenRepository
import org.apache.commons.validator.routines.EmailValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


@RestController
class AccountController(val accounts: AccountRepository, val tokens: TokenRepository) {

    private val log: Logger = LoggerFactory.getLogger(AccountController::class.java)

    val hashKeyLength = 256
    val hashIterations = 32
    val saltLength = 64
    val tokenLength = 128
    val random = SecureRandom()

    data class RegisterRequest(val email: String, val password: String)
    data class LoginRequest(val email: String, val password: String)
    data class AccountStatus(val email: String)

    @GetMapping("/account/status")
    fun status(@RequestHeader authorization: String): Mono<AccountStatus> {
        return accounts.findByToken(authorization)
                .map { AccountStatus(it.email) }
                .switchIfEmpty { Mono.error(InvalidCredentialsException()) }
    }

    @PostMapping("/account/register")
    fun register(@RequestBody body: RegisterRequest): Mono<Token> {
        val salt = ByteArray(saltLength)
                .apply { random.nextBytes(this) }
                .run { Base64.getEncoder().encodeToString(this) }
        val hash = hashPassword(body.password, salt)
        val account = Account(body.email, salt, hash)
        account.new = true

        if (!EmailValidator.getInstance(false, false).isValid(body.email))
            throw InvalidEmailException()
        if (body.password.length < 8)
            throw PasswordTooShortException()

        return accounts.findById(body.email)
                .doOnNext { throw AccountAlreadyRegisteredException() }
                .then(accounts.save(account))
                .flatMap {
                    log.info("New user account: {}", it.email)
                    assignNewToken(it)
                }
    }

    @PostMapping("/account/login")
    fun login(@RequestBody body: LoginRequest) = accounts
            .findById(body.email)
            .flatMap {
                if (hashPassword(body.password, it.salt) != it.hash) Mono.error(InvalidCredentialsException())
                else assignNewToken(it)
            }.doOnSuccess {
                log.info("Successful login from ${body.email}")
            }

    fun assignNewToken(account: Account): Mono<Token> {
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

    class InvalidCredentialsException() :
            ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid credentials.")
}
