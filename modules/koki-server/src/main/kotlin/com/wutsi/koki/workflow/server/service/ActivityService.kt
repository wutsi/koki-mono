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
        return dao.findByNameAndWorkflowId(name, workflow.id!!)
            ?: throw NotFoundException(Error(ErrorCode.WORKFLOW_ACTIVITY_NOT_FOUND))
    }

    fun getByIds(ids: List<Long>): List<ActivityEntity> {
        return dao.findAllById(ids).toList()
    }

    fun getByWorkflow(workflow: WorkflowEntity): List<ActivityEntity> {
        return dao.findByWorkflowId(workflow.id!!)
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        workflowIds: List<Long> = emptyList(),
        roleIds: List<Long> = emptyList(),
        messageIds: List<String> = emptyList(),
        formIds: List<String> = emptyList(),
        scriptIds: List<String> = emptyList(),
        events: List<String> = emptyList(),
        type: ActivityType? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<ActivityEntity> {
        val jql = StringBuilder("SELECT A FROM ActivityEntity A WHERE A.tenantId=:tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND A.id IN :ids")
        }
        if (workflowIds.isNotEmpty()) {
            jql.append(" AND A.workflowId IN :workflowIds")
        }
        if (roleIds.isNotEmpty()) {
            jql.append(" AND A.roleId IN :roleIds")
        }
        if (messageIds.isNotEmpty()) {
            jql.append(" AND A.messageId IN :messageIds")
        }
        if (formIds.isNotEmpty()) {
            jql.append(" AND A.formId IN :formIds")
        }
        if (scriptIds.isNotEmpty()) {
            jql.append(" AND A.scriptId IN :scriptIds")
        }
        if (events.isNotEmpty()) {
            jql.append(" AND UPPER(A.event) IN :events")
        }
        if (type != null) {
            jql.append(" AND A.type = :type")
        }
        if (active != null) {
            jql.append(" AND A.active = :active")
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
        if (roleIds.isNotEmpty()) {
            query.setParameter("roleIds", roleIds)
        }
        if (messageIds.isNotEmpty()) {
            query.setParameter("messageIds", messageIds)
        }
        if (formIds.isNotEmpty()) {
            query.setParameter("formIds", formIds)
        }
        if (scriptIds.isNotEmpty()) {
            query.setParameter("scriptIds", scriptIds)
        }
        if (events.isNotEmpty()) {
            query.setParameter("events", events.map { event -> event.uppercase() })
        }
        if (type != null) {
            query.setParameter("type", type)
        }
        if (active != null) {
            query.setParameter("active", active)
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
