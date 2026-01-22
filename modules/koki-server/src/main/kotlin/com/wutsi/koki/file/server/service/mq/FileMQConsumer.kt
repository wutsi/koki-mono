package com.wutsi.koki.file.server.service.mq

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.WutsiException
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import org.springframework.stereotype.Service

@Service
class FileMQConsumer(
    private val fileUploadedEventHandler: FileUploadedEventHandler,
    private val createFileCommandHandler: CreateFileCommandHandler,
    private val logger: KVLogger,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is FileUploadedEvent) {
            fileUploadedEventHandler.handle(event)
        } else if (event is CreateFileCommand) {
            try {
                createFileCommandHandler.handle(event)
            } catch (ex: WutsiException) {
                if (ex.error.code == ErrorCode.FILE_ALREADY_EXISTS) {
                    logger.add("warning", ex.error.code)
                    return false // Ignore duplicate file
                } else {
                    throw ex
                }
            }
        } else {
            return false
        }
        return true
    }
}
