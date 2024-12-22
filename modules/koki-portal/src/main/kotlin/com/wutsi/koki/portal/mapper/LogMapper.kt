package com.wutsi.koki.portal.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.portal.model.ActivityInstanceModel
import com.wutsi.koki.portal.model.LogEntryModel
import com.wutsi.koki.portal.model.WorkflowInstanceModel
import com.wutsi.koki.workflow.dto.LogEntry
import com.wutsi.koki.workflow.dto.LogEntrySummary
import org.springframework.stereotype.Service

@Service
class LogMapper(private val objectMapper: ObjectMapper) : TenantAwareMapper() {
    fun toLogEntryModel(
        entity: LogEntry,
        activityInstance: ActivityInstanceModel?,
        workflowInstance: WorkflowInstanceModel,
    ): LogEntryModel {
        val fmt = createDateFormat()

        return LogEntryModel(
            id = entity.id,
            message = entity.message,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            stackTrace = entity.stackTrace,
            metadata = entity.metadata,
            metadataJSON = if (entity.metadata.isEmpty()) {
                null
            } else {
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity.metadata)
            },
            type = entity.type,
            activityInstance = activityInstance,
            workflowInstance = workflowInstance,
        )
    }

    fun toLogEntryModel(
        entity: LogEntrySummary,
        activityInstance: ActivityInstanceModel?
    ): LogEntryModel {
        val fmt = createDateFormat()

        return LogEntryModel(
            id = entity.id,
            message = entity.message,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            type = entity.type,
            activityInstance = activityInstance,
        )
    }
}
