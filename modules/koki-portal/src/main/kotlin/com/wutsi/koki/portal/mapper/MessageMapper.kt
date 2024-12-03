package com.wutsi.koki.portal.mapper

import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.portal.model.MessageModel
import org.springframework.stereotype.Service
import java.text.DateFormat
import java.text.SimpleDateFormat

@Service
class MessageMapper {
    fun toMessageModel(entity: MessageSummary): MessageModel {
        val fmt = createDateFormat()
        return MessageModel(
            id = entity.id,
            name = entity.name,
            subject = entity.subject,
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
            body = entity.body,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    private fun createDateFormat(): DateFormat {
        return SimpleDateFormat("yyyy/MM/dd HH:mm")
    }
}