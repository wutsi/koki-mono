package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.ActivityInstanceModel
import com.wutsi.koki.portal.model.LogEntryModel
import com.wutsi.koki.workflow.dto.LogEntry
import com.wutsi.koki.workflow.dto.LogEntrySummary
import org.springframework.stereotype.Service
import java.text.DateFormat
import java.text.SimpleDateFormat

@Service
class LogMapper {
    fun toLogEntryModel(
        entity: LogEntry,
        activityInstance: ActivityInstanceModel?
    ): LogEntryModel {
        val fmt = createDateFormat()

        return LogEntryModel(
            id = entity.id,
            message = entity.message,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            stackTrace = entity.stackTrace,
            metadata = entity.metadata,
            type = entity.type,
            activityInstance = activityInstance,
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

    private fun createDateFormat(): DateFormat {
        return SimpleDateFormat("yyyy/MM/dd HH:mm")
    }
}
