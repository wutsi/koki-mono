package com.wutsi.koki.file.server.service.mq

import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.platform.mq.Consumer
import org.springframework.stereotype.Service

@Service
class FileMQConsumer(
    private val fileUploadedEventHandler: FileUploadedEventHandler,
    private val createFileCommandHandler: CreateFileCommandHandler,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is FileUploadedEvent) {
            fileUploadedEventHandler.handle(event)
        } else if (event is CreateFileCommand) {
            createFileCommandHandler.handle(event)
        } else {
            return false
        }
        return true
    }
}
