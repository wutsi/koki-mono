package com.wutsi.koki.workflow.server.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivitySummary
import com.wutsi.koki.workflow.dto.Recipient
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import org.springframework.stereotype.Service

@Service
class ActivityMapper(private val objectMapper: ObjectMapper) {
    fun toActivity(entity: ActivityEntity): Activity {
        return Activity(
            id = entity.id!!,
            workflowId = entity.workflowId,
            roleId = entity.roleId,
            formId = entity.formId,
            messageId = entity.messageId,
            scriptId = entity.scriptId,
            serviceId = entity.serviceId,
            event = entity.event,
            type = entity.type,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            active = entity.active,
            requiresApproval = entity.requiresApproval,
            path = entity.path,
            method = entity.method,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            input = entity.inputAsMap(objectMapper),
            output = entity.outputAsMap(objectMapper),
            recipient = entity.recipientEmail?.ifEmpty { null }?.let { email ->
                Recipient(
                    email = email, displayName = entity.recipientDisplayName
                )
            }
        )
    }

    fun toActivitySummary(entity: ActivityEntity): ActivitySummary {
        return ActivitySummary(
            id = entity.id!!,
            workflowId = entity.workflowId,
            roleId = entity.roleId,
            formId = entity.formId,
            messageId = entity.messageId,
            scriptId = entity.scriptId,
            serviceId = entity.serviceId,
            event = entity.event,
            type = entity.type,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            requiresApproval = entity.requiresApproval,
        )
    }
}
