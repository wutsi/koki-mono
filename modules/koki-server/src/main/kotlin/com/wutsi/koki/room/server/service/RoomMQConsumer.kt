package com.wutsi.koki.room.server.service

import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.room.server.command.PublishRoomCommand
import com.wutsi.koki.room.server.service.event.FileUploadedHandler
import org.springframework.stereotype.Service

@Service
class RoomMQConsumer(
    private val publishRoomHandler: PublishRoomCommandHandler,
    private val fileUploadedHandler: FileUploadedHandler,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is FileUploadedEvent) {
            fileUploadedHandler.handle(event)
        } else if (event is PublishRoomCommand) {
            publishRoomHandler.handle(event)
        } else {
            return false
        }
        return true
    }
}
