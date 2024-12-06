package com.wutsi.koki.workflow.server.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.workflow.dto.LogEntry
import com.wutsi.koki.workflow.dto.LogEntrySummary
import com.wutsi.koki.workflow.server.domain.LogEntryEntity
import org.springframework.stereotype.Service

@Service
class LogMapper(private val objectMapper: ObjectMapper) {
    fun toLogEntry(entity: LogEntryEntity): LogEntry {
        return LogEntry(
            id = entity.id ?: "",
            type = entity.type,
            message = entity.message,
            createdAt = entity.createdAt,
            workflowInstanceId = entity.workflowInstanceId,
            activityInstanceId = entity.activityInstanceId,
            stackTrace = entity.stackTrace,
            metadata = entity.metadataAsMap(objectMapper)
        )
    }

    fun toLogEntrySummary(entity: LogEntryEntity): LogEntrySummary {
        return LogEntrySummary(
            id = entity.id ?: "",
            type = entity.type,
            message = entity.message,
            createdAt = entity.createdAt,
            workflowInstanceId = entity.workflowInstanceId,
            activityInstanceId = entity.activityInstanceId,
        )
    }
}
