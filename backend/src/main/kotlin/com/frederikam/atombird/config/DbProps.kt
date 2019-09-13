package com.frederikam.atombird.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("db")
class DbProps (
        var host: String = "localhost",
        var port: Int = 5432,
        var database: String = "postgres",
        var username: String = "postgres",
        var password: String = ""
)
