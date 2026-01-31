package com.wutsi.koki.bot

import com.wutsi.koki.platform.KokiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@KokiApplication
@EnableScheduling
@SpringBootApplication
class KokiBotApplication

fun main(args: Array<String>) {
    runApplication<KokiBotApplication>(*args)
}
