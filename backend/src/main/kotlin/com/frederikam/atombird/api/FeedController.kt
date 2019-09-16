package com.frederikam.atombird.api

import com.frederikam.atombird.data.AccountRepository
import com.frederikam.atombird.data.Feed
import com.frederikam.atombird.data.FeedRepository
import com.frederikam.atombird.service.FeedFetcher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController("/feed")
class FeedController(
        private val feedFetcher: FeedFetcher,
        private val feeds: FeedRepository,
        private val accounts: AccountRepository
) {

    class NewFeedRequest(val url: String)

    @PostMapping("/new")
    fun newFeed(@RequestHeader authorization: String, @RequestBody body: NewFeedRequest): Mono<Feed> = accounts
            .findByTokenOrThrow(authorization)
            .flatMap { feedFetcher.fetchFeed(body.url) }
            .flatMap { feeds.save(it.feed) }

}
