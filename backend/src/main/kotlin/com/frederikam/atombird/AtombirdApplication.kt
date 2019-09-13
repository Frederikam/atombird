package com.frederikam.atombird

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
class AtombirdApplication

fun main(args: Array<String>) {
    runApplication<AtombirdApplication>(*args)
}
