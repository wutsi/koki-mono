package com.wutsi.koki.webscraping.server.service.mq

import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.webscraping.server.command.CreateWebpageListingCommand
import org.springframework.stereotype.Service

@Service
class WebscrapingMQConsumer(
    private val createWebpageListingCommandHandler: CreateWebpageListingCommandHandler,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is CreateWebpageListingCommand) {
            return createWebpageListingCommandHandler.handle(event)
        }
        return false
    }
}
