package com.frederikam.atombird.api

import com.frederikam.atombird.data.AccountRepository
import com.frederikam.atombird.data.Entry
import com.frederikam.atombird.data.EntryRepository
import com.frederikam.atombird.data.findByTokenOrThrow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

/**
 * A “stream” in this context refers to a combination of feeds. Basically the collection of entries that the user sees.
 */
@RestController
class StreamController(
        private val accountRepository: AccountRepository,
        private val entryRepository: EntryRepository
) {
    @GetMapping("/stream")
    fun get(
            @RequestHeader authorization: String,
            @RequestParam(defaultValue = "0") limit: Int,
            @RequestParam(defaultValue = "20") offset: Int
    ): Flux<Entry> = accountRepository
            .findByTokenOrThrow(authorization)
            .flatMapMany { entryRepository.findAllByAccount(it.id, limit, offset) }
}