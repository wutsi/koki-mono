package com.wutsi.koki.portal.message.mapper

import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.message.model.MessageModel
import com.wutsi.koki.portal.service.Moment
import org.springframework.stereotype.Service

@Service
class MessageMapper(private val moment: Moment) : TenantAwareMapper() {
    fun toMessageModel(entity: Message): MessageModel {
        val fmt = createDateTimeFormat()
        return MessageModel(
            id = entity.id,
            senderName = entity.senderName,
            senderEmail = entity.senderEmail,
            senderPhone = entity.senderPhone?.ifEmpty { null },
            body = entity.body,
            status = entity.status,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
            ownerId = entity.owner?.id,
            ownerType = entity.owner?.type,
        )
    }

    fun toMessageModel(entity: MessageSummary): MessageModel {
        val fmt = createDateTimeFormat()
        return MessageModel(
            id = entity.id,
            senderName = entity.senderName,
            senderEmail = entity.senderEmail,
            senderPhone = entity.senderPhone?.ifEmpty { null },
            status = entity.status,
            body = entity.body,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
            ownerId = entity.owner?.id,
            ownerType = entity.owner?.type,
        )
    }
}
