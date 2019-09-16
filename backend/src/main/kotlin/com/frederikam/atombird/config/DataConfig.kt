package com.frederikam.atombird.config

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ConnectionFactoryOptions.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration

@Configuration
class DataConfig(private val props: DbProps) : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory =
            ConnectionFactories.get(ConnectionFactoryOptions.builder()
                    .option(DRIVER, "postgresql")
                    .option(HOST, props.host)
                    .option(PORT, props.port)
                    .option(USER, props.username)
                    .option(PASSWORD, props.password)
                    .option(DATABASE, props.database)
                    .build());

}
