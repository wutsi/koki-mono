package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.server.dao.ActivityRepository
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class ActivityService(private val dao: ActivityRepository) {
    fun getByCode(code: String, workflow: WorkflowEntity): ActivityEntity {
        return dao.findByCodeIgnoreCaseAndWorkflow(code, workflow)
            ?: throw NotFoundException(Error(ErrorCode.WORKFLOW_ACTIVITY_NOT_FOUND))
    }

    fun getByCodes(codes: List<String>, workflow: WorkflowEntity): List<ActivityEntity> {
        return dao.findByCodeIgnoreCaseInAndWorkflow(codes, workflow)
    }

    fun getByWorkflow(workflow: WorkflowEntity): List<ActivityEntity> {
        return dao.findByWorkflow(workflow)
    }

    @Transactional
    fun save(activity: ActivityEntity): ActivityEntity {
        activity.modifiedAt = Date()
        return dao.save(activity)
    }
}
