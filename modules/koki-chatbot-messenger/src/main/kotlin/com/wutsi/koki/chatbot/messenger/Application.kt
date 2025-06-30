package com.wutsi.koki.chatbot.messenger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@ServletComponentScan
@EnableAspectJAutoProxy
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
