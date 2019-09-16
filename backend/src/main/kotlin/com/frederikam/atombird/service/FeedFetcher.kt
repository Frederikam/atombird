package com.frederikam.atombird.service

import com.frederikam.atombird.data.Entry
import com.frederikam.atombird.data.Feed
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class FeedFetcher {

    fun fetchFeed(url: String): Mono<FetchResult> {
        // TODO
        return Mono.just(FetchResult(Feed(
                null,
                "",
                "",
                "",
                null,
                null,
                emptyArray(),
                Instant.now(),
                Instant.now()
        ), emptyList()))
    }

    class FetchResult(val feed: Feed, val entries: List<Entry>)

}
