package com.frederikam.atombird.service

import com.frederikam.atombird.data.Entry
import com.frederikam.atombird.data.Feed
import com.frederikam.atombird.data.FeedRepository
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.reactor.monoResponse
import com.github.kittinunf.fuel.reactor.monoResultBytes
import com.github.kittinunf.fuel.reactor.monoResultString
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
class FeedFetcher(private val feeds: FeedRepository) {

    val fetchInterval = Duration.ofMinutes(5)!!

    // TODO: Check dupes
    fun fetchNewFeed(user: String, url: String, tags: Array<String>): Mono<Feed> = url.httpGet()
            .header("User-Agent", "Atombird")
            .monoResponse()
            .flatMap {
                if (!it.isSuccessful) {
                    throw ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to download feed:" + it.responseMessage
                    )
                }
                val etag = it.header("Etag").firstOrNull()
                val input = SyndFeedInput().build(XmlReader(it.data.inputStream()))
                val entity = input.toEntity(user, url, tags, null, etag)
                feeds.save(entity)
                // TODO: Save entries
            }

    private fun SyndFeed.toEntity(userId: String, url: String, tags: Array<String>, entityId: Long?, etag: String?) = Feed(
            entityId,
            url,
            userId,
            this.title,
            this.link,
            this.description,
            tags,
            Instant.now(),
            Instant.now() + fetchInterval,
            etag
    )

    /** If a fetch returns a 304 we should just update the checkAfter value */
    private fun onNotModified(entityId: Long) = feeds

}
