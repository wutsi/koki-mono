package com.wutsi.koki.message.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.message.dto.CreateMessageRequest
import com.wutsi.koki.message.dto.MessageSortBy
import com.wutsi.koki.message.dto.UpdateMessageRequest
import com.wutsi.koki.message.server.dao.MessageRepository
import com.wutsi.koki.message.server.domain.MessageEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class MessageService(
    private val dao: MessageRepository,
    private val em: EntityManager,
) {
    fun get(id: String, tenantId: Long): MessageEntity {
        val message = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.MESSAGE_NOT_FOUND)) }

        if (message.tenantId != tenantId || message.deleted) {
            throw NotFoundException(Error(ErrorCode.MESSAGE_NOT_FOUND))
        }
        return message
    }

    fun getByName(name: String, tenantId: Long): MessageEntity {
        val messages = search(
            tenantId = tenantId,
            names = listOf(name),
            limit = 1
        )
        if (messages.isEmpty()) {
            throw NotFoundException(
                Error(
                    ErrorCode.MESSAGE_NOT_FOUND,
                    parameter = Parameter(value = name),
                )
            )
        }
        return messages.first()
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: MessageSortBy? = null,
        ascending: Boolean = true,
    ): List<MessageEntity> {
        val jql = StringBuilder("SELECT M FROM MessageEntity M")
        jql.append(" WHERE M.deleted=false AND M.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND M.id IN :ids")
        }
        if (names.isNotEmpty()) {
            jql.append(" AND UPPER(M.name) IN :names")
        }
        if (active != null) {
            jql.append(" AND M.active = :active")
        }
        if (sortBy != null) {
            val column = when (sortBy) {
                MessageSortBy.NAME -> "name"
                MessageSortBy.SUBJECT -> "subject"
                MessageSortBy.CREATED_AT -> "createdAt"
                MessageSortBy.MODIFIED_AT -> "modifiedAt"
            }
            jql.append(" ORDER BY M.$column")
            if (!ascending) {
                jql.append(" DESC")
            }
        }

        val query = em.createQuery(jql.toString(), MessageEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (names.isNotEmpty()) {
            query.setParameter("names", names.map { name -> name.uppercase() })
        }
        if (active != null) {
            query.setParameter("active", active)
        }
        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateMessageRequest, tenantId: Long): MessageEntity {
        val duplicate = dao.findByNameIgnoreCaseAndTenantId(request.name, tenantId)
        if (duplicate != null) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.MESSAGE_DUPLICATE_NAME,
                    parameter = Parameter(value = request.name)
                )
            )
        }

        return dao.save(
            MessageEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                name = request.name,
                subject = request.subject,
                description = request.description,
                body = request.body,
                active = request.active,
            )
        )
    }

    @Transactional
    fun update(id: String, request: UpdateMessageRequest, tenantId: Long) {
        val duplicate = dao.findByNameIgnoreCaseAndTenantId(request.name, tenantId)
        if (duplicate != null && duplicate.id != id) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.MESSAGE_DUPLICATE_NAME,
                    parameter = Parameter(value = request.name)
                )
            )
        }

        val message = get(id, tenantId)
        message.name = request.name
        message.subject = request.subject
        message.description = request.description
        message.body = request.body
        message.active = request.active
        dao.save(message)
    }

    @Transactional
    fun delete(id: String, tenantId: Long) {
        val message = get(id, tenantId)
        message.name = "##-" + message.name + "-" + UUID.randomUUID().toString()
        message.deleted = true
        message.deletedAt = Date()
    }
}
