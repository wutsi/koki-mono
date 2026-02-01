package com.wutsi.koki.webscraping.server.service.mq

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.WutsiException
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.webscraping.server.command.CreateWebpageImagesCommand
import com.wutsi.koki.webscraping.server.command.CreateWebpageListingCommand
import org.springframework.stereotype.Service

@Service
class WebscrapingMQConsumer(
    private val createWebpageListingCommandHandler: CreateWebpageListingCommandHandler,
    private val createWebpageImagesCommandHandler: CreateWebpageImagesCommandHandler,
    private val logger: KVLogger,
) : Consumer {
    override fun consume(event: Any): Boolean {
        try {
            if (event is CreateWebpageListingCommand) {
                createWebpageListingCommandHandler.handle(event)
                return true
            } else if (event is CreateWebpageImagesCommand) {
                return createWebpageImagesCommandHandler.handle(event)
            } else {
                return false
            }
        } catch (ex: WutsiException) {
            when (ex.error.code) {
                ErrorCode.LISTING_ALREADY_CREATED,
                ErrorCode.LOCATION_NOT_FOUND,
                ErrorCode.LISTING_INVALID_TEXT,
                ErrorCode.WEBPAGE_NOT_FOUND -> {
                    logger.add("warning", ex.error.code)
                    return false
                }

                else -> throw ex
            }
        }
    }
}
