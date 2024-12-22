package com.wutsi.koki.portal.mapper

import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.portal.model.MessageModel
import org.springframework.stereotype.Service

@Service
class MessageMapper : TenantAwareMapper() {
    fun toMessageModel(entity: MessageSummary): MessageModel {
        val fmt = createDateFormat()
        return MessageModel(
            id = entity.id,
            name = entity.name,
            subject = entity.subject,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    fun toMessageModel(entity: Message): MessageModel {
        val fmt = createDateFormat()
        return MessageModel(
            id = entity.id,
            name = entity.name,
            subject = entity.subject,
            description = entity.description,
            body = entity.body,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }
}
