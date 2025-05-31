package com.wutsi.koki.room.server.service

import com.wutsi.koki.message.dto.event.MessageSentEvent
import com.wutsi.koki.notification.server.service.AbstractNotificationWorker
import com.wutsi.koki.notification.server.service.NotificationMQConsumer
import org.springframework.stereotype.Service

@Service
class RoomEmailNotificationWorker(
    registry: NotificationMQConsumer,

    private val messageSender: MessageEmailSender,
) : AbstractNotificationWorker(registry) {
    override fun notify(event: Any): Boolean {
        if (event is MessageSentEvent) {
            messageSender.send(event)
        } else {
            return false
        }
        return true
    }
}
