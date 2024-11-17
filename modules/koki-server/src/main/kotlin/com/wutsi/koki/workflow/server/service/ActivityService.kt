package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.server.dao.ActivityRepository
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class ActivityService(
    private val dao: ActivityRepository,
    private val em: EntityManager,
) {
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

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        workflowIds: List<Long> = emptyList(),
        type: ActivityType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<ActivityEntity> {
        val jql = StringBuilder("SELECT A FROM ActivityEntity A WHERE A.workflow.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND A.id IN :ids")
        }
        if (workflowIds.isNotEmpty()) {
            jql.append(" AND A.workflowId IN :workflowIds")
        }
        if (type != null) {
            jql.append(" AND A.type = :type")
        }
        jql.append(" ORDER BY A.createdAt DESC")

        val query = em.createQuery(jql.toString(), ActivityEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (workflowIds.isNotEmpty()) {
            query.setParameter("workflowIds", workflowIds)
        }
        if (type != null) {
            query.setParameter("type", type)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
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
