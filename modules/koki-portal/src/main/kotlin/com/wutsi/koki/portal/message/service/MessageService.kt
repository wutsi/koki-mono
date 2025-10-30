package com.wutsi.koki.portal.message.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.UpdateMessageStatusRequest
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import com.wutsi.koki.portal.common.service.ObjectReferenceService
import com.wutsi.koki.portal.message.mapper.MessageMapper
import com.wutsi.koki.portal.message.model.MessageModel
import com.wutsi.koki.sdk.KokiMessages
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val koki: KokiMessages,
    private val mapper: MessageMapper,
    private val objectReferenceService: ObjectReferenceService,
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

        val references = mutableMapOf<Long, ObjectReferenceModel>()
        val ownerTypes = msgs.mapNotNull { msg -> msg.owner?.type }.distinct()
        ownerTypes.forEach { objectType ->
            val ids = msgs
                .filter { msg -> msg.owner?.type == objectType }
                .mapNotNull { msg -> msg.owner?.id }
            val refs = objectReferenceService.references(ids, objectType)
            references.putAll(refs.associateBy { ref -> ref.id })
        }

        return msgs.map { msg -> mapper.toMessageModel(msg, references) }
    }

    fun message(id: Long): MessageModel {
        val msg = koki.message(id).message
        val owner = msg.owner?.let { owner -> objectReferenceService.reference(owner.id, owner.type) }
        return mapper.toMessageModel(msg, owner)
    }

    fun status(id: Long, status: MessageStatus) {
        koki.status(id, UpdateMessageStatusRequest(status))
    }
}
