package com.wutsi.koki.listing.server.service.mq

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.WutsiException
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import org.springframework.stereotype.Service

@Service
class ListingMQConsumer(
    private val fileUploadedEventHandler: ListingFileUploadedEventHandler,
    private val fileDeletedEventHandler: ListingFileDeletedEventHandler,
    private val listingStatusChangedEventHandler: ListingStatusChangedEventHandler,
    private val leadMessageReceivedEventHandler: ListingLeadMessageReceivedEventHandler,
    private val logger: KVLogger,
) : Consumer {
    override fun consume(event: Any): Boolean {
        try {
            return if (event is FileUploadedEvent) {
                fileUploadedEventHandler.handle(event)
            } else if (event is FileDeletedEvent) {
                fileDeletedEventHandler.handle(event)
            } else if (event is ListingStatusChangedEvent) {
                listingStatusChangedEventHandler.handle(event)
            } else if (event is LeadMessageReceivedEvent) {
                leadMessageReceivedEventHandler.handle(event)
            } else {
                false
            }
        } catch (ex: WutsiException) {
            when (ex.error.code) {
                ErrorCode.FILE_NOT_FOUND -> {
                    logger.add("warning", ex.error.code)
                    return false
                }

                else -> throw ex
            }
        }
    }
}
