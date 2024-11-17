package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.ActivityModel
import com.wutsi.koki.workflow.dto.ActivitySummary

class WorkflowMapper {
    fun toActivityModel(entity: ActivitySummary): ActivityModel {
        return ActivityModel(
            id = entity.id,
            workflowId = entity.workflowId,
            name = entity.name,
            title = entity.title ?: "",
            type = entity.type,
        )
    }
}
