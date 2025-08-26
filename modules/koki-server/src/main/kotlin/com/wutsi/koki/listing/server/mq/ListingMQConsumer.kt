package com.wutsi.koki.listing.server.mq

import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.platform.mq.Consumer
import org.springframework.stereotype.Service

@Service
class ListingMQConsumer(
    private val fileUploadedEventHandler: ListingFileUploadedEventHandler
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is FileUploadedEvent) {
            fileUploadedEventHandler.handle(event)
        } else {
            return false
        }
        return true
    }
}
