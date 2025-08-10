package com.wutsi.koki.portal.message.mapper

import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import com.wutsi.koki.portal.common.service.Moment
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.message.model.MessageModel
import org.springframework.stereotype.Service

@Service
class MessageMapper(private val moment: Moment) : TenantAwareMapper() {
    fun toMessageModel(entity: Message, owner: ObjectReferenceModel?): MessageModel {
        val fmt = createDateTimeFormat()
        return MessageModel(
            id = entity.id,
            senderName = entity.senderName,
            senderEmail = entity.senderEmail,
            senderPhone = entity.senderPhone?.ifEmpty { null },
            body = entity.body,
            status = entity.status,
            country = entity.country,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
            owner = owner,
        )
    }

    fun toMessageModel(entity: MessageSummary, owners: Map<Long, ObjectReferenceModel>): MessageModel {
        val fmt = createDateTimeFormat()
        return MessageModel(
            id = entity.id,
            senderName = entity.senderName,
            senderEmail = entity.senderEmail,
            senderPhone = entity.senderPhone?.ifEmpty { null },
            status = entity.status,
            body = entity.body,
            country = entity.country,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
            owner = entity.owner?.id?.let { id -> owners[id] },
        )
    }
}
