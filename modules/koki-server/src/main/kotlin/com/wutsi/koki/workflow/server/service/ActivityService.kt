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
    fun get(id: Long): ActivityEntity {
        return dao.findById(id).orElseThrow {
            NotFoundException(Error(ErrorCode.WORKFLOW_ACTIVITY_NOT_FOUND))
        }
    }

    fun getByName(name: String, workflow: WorkflowEntity): ActivityEntity {
        return dao.findByNameAndWorkflow(name, workflow)
            ?: throw NotFoundException(Error(ErrorCode.WORKFLOW_ACTIVITY_NOT_FOUND))
    }

    fun getByIds(ids: List<Long>): List<ActivityEntity> {
        return dao.findAllById(ids).toList()
    }

    fun getByWorkflow(workflow: WorkflowEntity): List<ActivityEntity> {
        return dao.findByWorkflow(workflow)
    }

    @Transactional
    fun save(activity: ActivityEntity): ActivityEntity {
        activity.modifiedAt = Date()
        return dao.save(activity)
    }

    @Transactional
    fun saveAll(activities: List<ActivityEntity>): List<ActivityEntity> {
        val now = Date()
        activities.forEach { activity -> activity.modifiedAt = now }
        dao.saveAll(activities)
        return activities
    }
}
