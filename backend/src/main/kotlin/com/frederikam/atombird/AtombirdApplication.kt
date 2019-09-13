package com.frederikam.atombird

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AtombirdApplication

fun main(args: Array<String>) {
    runApplication<AtombirdApplication>(*args)
}
