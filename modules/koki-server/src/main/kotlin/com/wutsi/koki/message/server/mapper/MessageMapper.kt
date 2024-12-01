package com.wutsi.koki.message.server.mapper

import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.message.server.domain.MessageEntity
import org.springframework.stereotype.Service

@Service
class MessageMapper {
    fun toMessage(entity: MessageEntity): Message {
        return Message(
            id = entity.id ?: "",
            name = entity.name,
            subject = entity.subject,
            body = entity.body,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toMessageSummary(entity: MessageEntity): MessageSummary {
        return MessageSummary(
            id = entity.id ?: "",
            name = entity.name,
            subject = entity.subject,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
