package com.wutsi.koki.room.server.service

import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.message.dto.event.MessageSentEvent
import com.wutsi.koki.notification.server.service.AbstractNotificationWorker
import com.wutsi.koki.notification.server.service.NotificationMQConsumer
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import org.springframework.stereotype.Service

@Service
class RoomNotificationWorker(
    registry: NotificationMQConsumer,

    private val roomService: RoomService,
    private val accountService: AccountService,
    private val messagingServiceBuilder: MessagingServiceBuilder,
) : AbstractNotificationWorker(registry) {
    override fun notify(event: Any): Boolean{
        if (event is MessageSentEvent){
            onMessageSent(event)
        } else {
            return false
        }
        return true
    }

    fun onMessageSent(event: MessageSentEvent){

    }
}
