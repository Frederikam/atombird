package com.frederikam.atombird.data

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.nio.ByteBuffer

interface AccountRepository : ReactiveCrudRepository<Account, String>
interface TokenRepository : ReactiveCrudRepository<Token, String>

class Account(
        @Id val email: String,
        val salt: String,
        var hash: String,
        @Transient var new: Boolean = false
) : Persistable<String> {
    override fun getId() = email
    override fun isNew() = new
}

class Token(
        @Id val token: String,
        val email: String
) : Persistable<String> {
    @JsonIgnore override fun getId() = email
    @JsonIgnore override fun isNew() = true
}