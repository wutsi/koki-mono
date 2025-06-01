package com.wutsi.koki.room.web.message.service

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.SendMessageRequest
import com.wutsi.koki.room.web.message.form.SendMessageForm
import com.wutsi.koki.sdk.KokiMessages
import org.springframework.stereotype.Service

@Service
class MessageService(private val koki: KokiMessages) {
    fun send(form: SendMessageForm) {
        koki.send(
            SendMessageRequest(
                senderName = form.name,
                senderPhone = form.fullPhone,
                senderEmail = form.email,
                owner = ObjectReference(id = form.roomId, type = ObjectType.ROOM),
                body = form.body,
            )
        )
    }
}
