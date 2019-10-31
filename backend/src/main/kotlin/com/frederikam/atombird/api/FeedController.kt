package com.frederikam.atombird.api

import com.frederikam.atombird.data.AccountRepository
import com.frederikam.atombird.data.Feed
import com.frederikam.atombird.data.FeedRepository
import com.frederikam.atombird.data.findByTokenOrThrow
import com.frederikam.atombird.service.FeedFetcher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class FeedController(
        private val feedFetcher: FeedFetcher,
        private val feeds: FeedRepository,
        private val accounts: AccountRepository
) {

    private val log: Logger = LoggerFactory.getLogger(FeedController::class.java)
    class NewFeedRequest(val url: String, val tags: Array<String>)

    @PostMapping("/feed/new")
    fun newFeed(@RequestHeader authorization: String, @RequestBody body: NewFeedRequest): Mono<Feed> = accounts
            .findByTokenOrThrow(authorization)
            .flatMap { feedFetcher.fetchNewFeed(it, body.url, body.tags) }
            .flatMap { feeds.save(it) }
            .doOnSuccess { log.info("Added feed ${body.url}") }

}
