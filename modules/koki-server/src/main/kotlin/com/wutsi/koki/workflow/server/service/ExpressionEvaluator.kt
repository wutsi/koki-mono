package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.springframework.stereotype.Service

@Service
class ActivityInstanceService(private val dao: ActivityInstanceRepository) {
    fun getById(id: String, workflowInstance: WorkflowInstanceEntity): ActivityInstanceEntity {
        val activityInstance = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_NOT_FOUND)) }

        if (activityInstance.instance.id != workflowInstance.id) {
            throw NotFoundException(Error(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_NOT_FOUND))
        }
        return activityInstance
    }

    fun save(activityInstance: ActivityInstanceEntity): ActivityInstanceEntity {
        return dao.save(activityInstance)
    }
}
