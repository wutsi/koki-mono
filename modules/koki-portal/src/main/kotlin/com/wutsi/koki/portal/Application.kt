package com.wutsi.koki.portal

import com.wutsi.koki.platform.KokiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@ServletComponentScan
@EnableAspectJAutoProxy
@KokiApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
