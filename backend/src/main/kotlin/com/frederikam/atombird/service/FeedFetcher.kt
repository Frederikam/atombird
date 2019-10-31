package com.frederikam.atombird.service

import com.frederikam.atombird.data.Account
import com.frederikam.atombird.data.Entry
import com.frederikam.atombird.data.EntryRepository
import com.frederikam.atombird.data.Feed
import com.frederikam.atombird.data.FeedRepository
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.reactor.monoResponse
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant

@Service
class FeedFetcher(private val feeds: FeedRepository, private val entryRepository: EntryRepository, private val sockets: WebSocketService) {

    private val log: Logger = LoggerFactory.getLogger(FeedFetcher::class.java)
    val fetchInterval = Duration.ofMinutes(5)!! // TODO: Make longer

    // TODO: Check dupes
    fun fetchNewFeed(account: Account, url: String, tags: Array<String>): Mono<Feed> = url.httpGet()
            .header("User-Agent", "Atombird")
            .monoResponse()
            .flatMap {
                // TODO handle 304
                if (!it.isSuccessful) {
                    throw ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to download feed:" + it.responseMessage
                    )
                }
                val etag = it.header("Etag").firstOrNull()
                @Suppress("BlockingMethodInNonBlockingContext")
                val feed = SyndFeedInput().build(XmlReader(it.data.inputStream()))
                handleFeed(account, null, feed, url, tags, etag)
            }

    private fun handleFeed(
            account: Account,
            oldData: Feed?, // Null if this is the first fetch for this URL and user
            newData: SyndFeed,
            url: String,
            tags: Array<String>,
            etag: String?
    ): Mono<Feed> {
        val newEntity = newData.run {
            Feed(
                    oldData?.id,
                    url,
                    account.email,
                    this.title,
                    this.link,
                    this.description,
                    tags,
                    Instant.now(),
                    Instant.now() + fetchInterval,
                    etag
            )
        }

        return feeds.save(newEntity).doOnSuccess { feed ->
            if (oldData == null) {
                entryRepository.saveAll(newData.entries.map { entryToEntity(null, it, feed.id!!) })
                        .subscribe { log.info("Saved entry: {}", it.nativeId) }
                return@doOnSuccess
            }

            // TODO: Handle re-fetches
        }
    }

    private fun entryToEntity(oldData: Entry?, newData: SyndEntry, feedId: Long) = newData.run {
        Entry(
                oldData?.id,
                feedId,
                newData.uri ?: newData.links.first().href,
                newData.updatedDate?.toInstant() ?: newData.publishedDate?.toInstant(),
                newData.link,
                newData.title,
                newData.description?.value,
                newData.contents.firstOrNull()?.value,
                newData.titleEx?.type,
                newData.description?.type,
                newData.contents.firstOrNull()?.type,
                newData.authors.firstOrNull()?.name,
                newData.authors.firstOrNull()?.email,
                newData.authors.firstOrNull()?.uri,
                oldData?.read ?: false
        )
    }

}
