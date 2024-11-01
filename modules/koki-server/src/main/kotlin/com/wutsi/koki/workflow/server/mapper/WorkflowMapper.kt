package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.stereotype.Service

@Service
class WorkflowMapper(private val activityMapper: ActivityMapper) {
    fun toWorkflow(entity: WorkflowEntity): Workflow {
        return Workflow(
            id = entity.id ?: -1,
            name = entity.name,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            activities = entity.activities.map { activity -> activityMapper.toActivity(activity) }
        )
    }
}
