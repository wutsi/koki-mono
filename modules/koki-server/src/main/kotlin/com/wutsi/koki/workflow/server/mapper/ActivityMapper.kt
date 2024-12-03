package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivitySummary
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import org.springframework.stereotype.Service
import java.io.StringReader
import java.util.Properties

@Service
class ActivityMapper {
    fun toActivity(entity: ActivityEntity): Activity {
        return Activity(
            id = entity.id!!,
            workflowId = entity.workflowId,
            roleId = entity.roleId,
            formId = entity.formId,
            messageId = entity.messageId,
            type = entity.type,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            active = entity.active,
            requiresApproval = entity.requiresApproval,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            tags = entity.tags?.let { tags -> toMap(tags) } ?: emptyMap(),
        )
    }

    fun toActivitySummary(entity: ActivityEntity): ActivitySummary {
        return ActivitySummary(
            id = entity.id!!,
            workflowId = entity.workflowId,
            roleId = entity.roleId,
            formId = entity.formId,
            messageId = entity.messageId,
            type = entity.type,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            requiresApproval = entity.requiresApproval,
        )
    }

    private fun toMap(tags: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val properties: Properties = Properties()
        properties.load(StringReader(tags))
        properties.keys().toList().map { key ->
            val name = key.toString()
            val value = properties.getProperty(name)
            if (value.isNotEmpty()) {
                map[name] = value
            }
        }
        return map
    }
}
