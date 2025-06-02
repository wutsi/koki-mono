package com.wutsi.koki.message.server.mapper

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.message.server.domain.MessageEntity
import org.springframework.stereotype.Service

@Service
class MessageMapper {
    fun toMessage(entity: MessageEntity): Message {
        return Message(
            id = entity.id ?: -1,
            senderName = entity.senderName,
            senderEmail = entity.senderEmail,
            senderPhone = entity.senderPhone,
            body = entity.body,
            status = entity.status,
            createdAt = entity.createdAt,
            owner = if (entity.ownerId != null && entity.ownerType != null) {
                ObjectReference(entity.ownerId, entity.ownerType)
            } else {
                null
            },
            language = entity.language,
            country = entity.country,
            cityId = entity.cityId,
            senderAccountId = entity.senderAccountId,
        )
    }

    fun toMessageSummary(entity: MessageEntity): MessageSummary {
        return MessageSummary(
            id = entity.id ?: -1,
            senderName = entity.senderName,
            senderEmail = entity.senderEmail,
            senderPhone = entity.senderPhone,
            status = entity.status,
            body = entity.body,
            createdAt = entity.createdAt,
            owner = if (entity.ownerId != null && entity.ownerType != null) {
                ObjectReference(entity.ownerId, entity.ownerType)
            } else {
                null
            },
            senderAccountId = entity.senderAccountId,
        )
    }
}
