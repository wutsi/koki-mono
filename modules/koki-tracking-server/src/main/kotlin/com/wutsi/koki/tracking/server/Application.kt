package com.wutsi.koki.tracking.server

import com.wutsi.koki.platform.KokiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
@KokiApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
