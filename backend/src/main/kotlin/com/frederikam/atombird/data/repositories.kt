package com.frederikam.atombird.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.frederikam.atombird.api.AccountController
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

val authFailMono = Mono.error<Account>(AccountController.InvalidCredentialsException())

interface AccountRepository : ReactiveCrudRepository<Account, String> {
    @Query("SELECT * FROM account WHERE email LIKE (SELECT email FROM token WHERE token.token LIKE :token)")
    fun findByToken(token: String): Mono<Account>
}

/** Throws exception if token is invalid */
fun AccountRepository.findByTokenOrThrow(token: String?): Mono<Account> {
    if (token.isNullOrBlank()) return authFailMono;
    return findByToken(token).switchIfEmpty(authFailMono)
}

interface TokenRepository : ReactiveCrudRepository<Token, String>

interface FeedRepository : ReactiveCrudRepository<Feed, Long> {
    @Query("SELECT * FROM feed WHERE user_id LIKE :user")
    fun findAllByUser(user: String)
}

interface EntryRepository : ReactiveCrudRepository<Entry, Long> {
    @Query("""
SELECT * FROM entry 
WHERE feed_id IN :feeds 
ORDER BY time DESC 
LIMIT :limit OFFSET :offset""")
    fun findAllByFeeds(feeds: Array<String>, limit: Int = 30, offset: Int = 0): Flux<Entry>
    @Query("""
SELECT * FROM entry 
WHERE feed_id IN (SELECT feed.id FROM feed WHERE feed.user_id LIKE :userId) 
ORDER BY time DESC 
LIMIT :limit OFFSET :offset""")
    fun findAllByAccount(userId: String, limit: Int = 30, offset: Int = 0): Flux<Entry>
}

@Component
class Repositories(accounts: AccountRepository, tokens: TokenRepository)

class Account(
        @Id val email: String,
        val salt: String,
        var hash: String
) : Persistable<String> {
    @Transient
    var new = false

    override fun getId() = email
    override fun isNew() = new
}

class Token(
        @Id val token: String,
        private val email: String
) : Persistable<String> {
    @JsonIgnore
    override fun getId() = email

    @JsonIgnore
    override fun isNew() = true
}

class Feed(
        @Id var id: Long?,
        /** URL of the feed */
        val url: String,
        /** The owner's email */
        @JsonIgnore
        val userId: String,
        var title: String,
        var uiLink: String?,
        var description: String?,
        val tags: Array<String>,
        @JsonIgnore
        var lastChecked: Instant,
        @JsonIgnore
        var checkAfter: Instant,
        /** Less time to spend on parsing */
        var etag: String?
)

class Entry(
        @Id var id: Long?,
        val feedId: Long,
        val nativeId: String,
        val time: Instant?,
        val url: String?,
        val title: String?,
        val summary: String?,
        val content: String?,
        val titleType: String?,
        val summaryType: String?,
        val contentType: String?,
        val imageUrl: String?,
        val authorName: String?,
        val authorEmail: String?,
        val authorUrl: String?,
        var read: Boolean?
)
