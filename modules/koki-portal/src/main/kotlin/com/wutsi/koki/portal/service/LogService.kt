package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.mapper.LogMapper
import com.wutsi.koki.portal.model.LogEntryModel
import com.wutsi.koki.sdk.KokiLogs
import org.springframework.stereotype.Service

@Service
class LogService(
    private val koki: KokiLogs,
    private val mapper: LogMapper,
    private val workflowInstanceService: WorkflowInstanceService,
) {
    fun logs(
        workflowInstanceId: String? = null,
        activityInstanceId: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<LogEntryModel> {
        val entries = koki.logs(
            workflowInstanceId = workflowInstanceId,
            activityInstanceId = activityInstanceId,
            limit = limit,
            offset = offset
        ).logEntries
        if (entries.isEmpty()) {
            return emptyList()
        }

        val activityInstanceIds = entries.mapNotNull { entry -> entry.activityInstanceId }.toSet()
        val activityInstanceMap = if (activityInstanceIds.isNotEmpty()) {
            workflowInstanceService.activities(
                ids = activityInstanceIds.toList(),
                limit = activityInstanceIds.size
            ).associateBy { activityInstance -> activityInstance.id }
        } else {
            emptyMap()
        }

        return entries.map { entry ->
            mapper.toLogEntryModel(
                entity = entry,
                activityInstance = activityInstanceMap[entry.activityInstanceId]
            )
        }
    }

    fun log(id: String): LogEntryModel {
        val entry = koki.log(id).logEntry
        val activityInstance = if (entry.activityInstanceId != null) {
            workflowInstanceService.activities(
                ids = listOf(entry.activityInstanceId!!),
                limit = 1
            ).firstOrNull()
        } else {
            null
        }

        return mapper.toLogEntryModel(
            entity = entry,
            activityInstance = activityInstance,
        )
    }
}
