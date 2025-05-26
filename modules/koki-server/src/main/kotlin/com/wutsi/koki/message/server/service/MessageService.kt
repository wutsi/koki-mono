package com.wutsi.koki.message.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.SendMessageRequest
import com.wutsi.koki.message.dto.UpdateMessageStatusRequest
import com.wutsi.koki.message.server.dao.MessageRepository
import com.wutsi.koki.message.server.domain.MessageEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class MessageService(
    private val dao: MessageRepository,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): MessageEntity {
        val msg = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.MESSAGE_NOT_FOUND)) }

        if (msg.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.MESSAGE_NOT_FOUND))
        }
        return msg
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        statuses: List<MessageStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<MessageEntity> {
        val jql = StringBuilder("SELECT M FROM MessageEntity AS M")

        jql.append(" WHERE M.tenantId=:tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND M.id IN :ids")
        }
        if (ownerId != null) {
            jql.append(" AND M.ownerId = :ownerId")
        }
        if (ownerType != null) {
            jql.append(" AND M.ownerType = :ownerType")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND M.status IN :statuses")
        }
        jql.append(" ORDER BY M.createdAt DESC")

        val query = em.createQuery(jql.toString(), MessageEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (ownerId != null) {
            query.setParameter("ownerId", ownerId)
        }
        if (ownerType != null) {
            query.setParameter("ownerType", ownerType)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun send(request: SendMessageRequest, tenantId: Long): MessageEntity {
        return dao.save(
            MessageEntity(
                tenantId = tenantId,
                ownerId = request.owner?.id,
                ownerType = request.owner?.type,
                senderName = request.senderName,
                senderPhone = request.senderPhone,
                senderEmail = request.senderEmail.lowercase(),
                body = request.body,
                createdAt = Date(),
                status = MessageStatus.NEW,
            )
        )
    }

    @Transactional
    fun status(id: Long, request: UpdateMessageStatusRequest, tenantId: Long): MessageEntity {
        val message = get(id, tenantId)
        message.status = request.status
        return dao.save(message)
    }
}
