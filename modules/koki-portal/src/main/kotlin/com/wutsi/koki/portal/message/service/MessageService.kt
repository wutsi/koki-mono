package com.wutsi.koki.portal.message.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.UpdateMessageStatusRequest
import com.wutsi.koki.portal.message.mapper.MessageMapper
import com.wutsi.koki.portal.message.model.MessageModel
import com.wutsi.koki.sdk.KokiMessages
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val koki: KokiMessages,
    private val mapper: MessageMapper,
) {
    fun messages(
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        statuses: List<MessageStatus> = emptyList(),
        limit: Int = 10,
        offset: Int = 20,
    ): List<MessageModel> {
        val msgs = koki.messages(
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            statuses = statuses,
            limit = limit,
            offset = offset
        ).messages
        return msgs.map { msg -> mapper.toMessageModel(msg) }
    }

    fun message(id: Long): MessageModel {
        val msg = koki.message(id).message
        return mapper.toMessageModel(msg)
    }

    fun status(id: Long, status: MessageStatus) {
        koki.status(id, UpdateMessageStatusRequest(status))
    }
}
