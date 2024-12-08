package com.wutsi.koki.portal.service

import com.wutsi.koki.message.dto.CreateMessageRequest
import com.wutsi.koki.message.dto.UpdateMessageRequest
import com.wutsi.koki.portal.mapper.MessageMapper
import com.wutsi.koki.portal.model.MessageModel
import com.wutsi.koki.portal.page.settings.message.MessageForm
import com.wutsi.koki.sdk.KokiMessages
import com.wutsi.koki.workflow.dto.MessageSortBy
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val koki: KokiMessages,
    private val mapper: MessageMapper,
) {
    fun message(id: String): MessageModel {
        return mapper.toMessageModel(koki.message(id).message)
    }

    fun delete(id: String) {
        koki.delete(id)
    }

    fun update(id: String, form: MessageForm) {
        koki.update(
            id, UpdateMessageRequest(
                name = form.name,
                subject = form.subject,
                body = form.body,
                active = form.active,
                description = form.description,
            )
        )
    }

    fun create(form: MessageForm): String {
        return koki.create(
            CreateMessageRequest(
                name = form.name,
                subject = form.subject,
                body = form.body,
                active = form.active,
                description = form.description,
            )
        ).messageId
    }

    fun messages(
        ids: List<String> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: MessageSortBy? = null,
        ascending: Boolean = true
    ): List<MessageModel> {
        val messages = koki.messages(
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
            ascending = ascending,
        ).messages

        return messages.map { message -> mapper.toMessageModel(entity = message) }
    }
}
