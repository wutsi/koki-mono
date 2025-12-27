package com.wutsi.koki

import com.wutsi.koki.platform.KokiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableCaching
@KokiApplication
@EntityScan(
    basePackages = [
        "com.wutsi.koki.account.server.domain",
        "com.wutsi.koki.agent.server.domain",
        "com.wutsi.koki.contact.server.domain",
        "com.wutsi.koki.file.server.domain",
        "com.wutsi.koki.lead.server.domain",
        "com.wutsi.koki.listing.server.domain",
        "com.wutsi.koki.message.server.domain",
        "com.wutsi.koki.module.server.domain",
        "com.wutsi.koki.note.server.domain",
        "com.wutsi.koki.offer.server.domain",
        "com.wutsi.koki.place.server.domain",
        "com.wutsi.koki.refdata.server.domain",
        "com.wutsi.koki.tenant.server.domain",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.wutsi.koki.account.server.dao",
        "com.wutsi.koki.agent.server.dao",
        "com.wutsi.koki.contact.server.dao",
        "com.wutsi.koki.file.server.dao",
        "com.wutsi.koki.lead.server.dao",
        "com.wutsi.koki.listing.server.dao",
        "com.wutsi.koki.module.server.dao",
        "com.wutsi.koki.note.server.dao",
        "com.wutsi.koki.offer.server.dao",
        "com.wutsi.koki.place.server.dao",
        "com.wutsi.koki.refdata.server.dao",
        "com.wutsi.koki.tenant.server.dao",
    ],
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
