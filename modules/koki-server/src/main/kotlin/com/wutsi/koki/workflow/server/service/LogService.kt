package com.wutsi.koki.workflow.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.dto.LogEntryType
import com.wutsi.koki.workflow.server.dao.LogEntryRepository
import com.wutsi.koki.workflow.server.domain.LogEntryEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date
import java.util.UUID

@Service
class LogService(
    private val dao: LogEntryRepository,
    private val objectMapper: ObjectMapper,
    private val em: EntityManager,
) {
    fun get(id: String, tenantId: Long): LogEntryEntity {
        val log = dao.findById(id)
            .orElseThrow {
                NotFoundException(Error(ErrorCode.LOG_NOT_FOUND))
            }
        if (tenantId != log.tenantId) {
            throw NotFoundException(Error(ErrorCode.LOG_NOT_FOUND))
        }
        return log
    }

    fun info(
        message: String,
        workflowInstanceId: String,
        tenantId: Long,
        timestamp: Long = System.currentTimeMillis(),
        activityInstanceId: String? = null,
        metadata: Map<String, Any> = emptyMap(),
    ) {
        create(
            tenantId = tenantId,
            type = LogEntryType.INFO,
            message = message,
            workflowInstanceId = workflowInstanceId,
            activityInstanceId = activityInstanceId,
            timestamp = timestamp,
            metadata = metadata,
        )
    }

    @Transactional
    fun error(
        message: String,
        workflowInstanceId: String,
        tenantId: Long,
        timestamp: Long = System.currentTimeMillis(),
        activityInstanceId: String? = null,
        metadata: Map<String, Any> = emptyMap(),
        ex: Throwable
    ) {
        create(
            tenantId = tenantId,
            type = LogEntryType.ERROR,
            message = message,
            workflowInstanceId = workflowInstanceId,
            activityInstanceId = activityInstanceId,
            exception = ex,
            timestamp = timestamp,
            metadata = metadata,
        )
    }

    @Transactional
    fun create(
        tenantId: Long,
        type: LogEntryType,
        message: String,
        workflowInstanceId: String,
        activityInstanceId: String? = null,
        metadata: Map<String, Any>? = null,
        exception: Throwable? = null,
        timestamp: Long = System.currentTimeMillis()
    ): LogEntryEntity {
        return dao.save(
            LogEntryEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                type = type,
                message = message,
                workflowInstanceId = workflowInstanceId,
                activityInstanceId = activityInstanceId,
                metadata = metadata?.let { map -> objectMapper.writeValueAsString(map) },
                stackTrace = exception?.let { ex -> toStackTrace(ex) },
                createdAt = Date(timestamp)
            )
        )
    }

    fun search(
        tenantId: Long,
        workflowInstanceId: String? = null,
        activityInstanceId: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<LogEntryEntity> {
        val jql = StringBuilder("SELECT L FROM LogEntryEntity L WHERE L.tenantId = :tenantId")

        if (workflowInstanceId != null) {
            jql.append(" AND L.workflowInstanceId IN :workflowInstanceId")
        }
        if (activityInstanceId != null) {
            jql.append(" AND L.activityInstanceId IN :activityInstanceId")
        }
        jql.append(" ORDER BY L.createdAt DESC")

        val query = em.createQuery(jql.toString(), LogEntryEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (workflowInstanceId != null) {
            query.setParameter("workflowInstanceId", workflowInstanceId)
        }
        if (activityInstanceId != null) {
            query.setParameter("activityInstanceId", activityInstanceId)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    private fun toStackTrace(ex: Throwable): String {
        val writer = StringWriter()
        val printer = PrintWriter(writer)
        ex.printStackTrace(printer)

        return writer.toString()
    }
}
