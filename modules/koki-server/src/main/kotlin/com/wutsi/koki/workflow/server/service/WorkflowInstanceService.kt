package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.WorkflowInstanceSortBy
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ParameterRepository
import com.wutsi.koki.workflow.server.dao.ParticipantRepository
import com.wutsi.koki.workflow.server.dao.StateRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.domain.ParameterEntity
import com.wutsi.koki.workflow.server.domain.ParticipantEntity
import com.wutsi.koki.workflow.server.domain.StateEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@Service
class WorkflowInstanceService(
    private val instanceDao: WorkflowInstanceRepository,
    private val participantDao: ParticipantRepository,
    private val stateDao: StateRepository,
    private val parameterDao: ParameterRepository,
    private val workflowService: WorkflowService,
    private val userService: UserService,
    private val roleService: RoleService,
    private val em: EntityManager,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowInstanceService::class.java)
    }

    fun get(id: String, tenantId: Long): WorkflowInstanceEntity {
        val instance = instanceDao.findById(id)
            .orElseThrow { NotFoundException(Error(code = ErrorCode.WORKFLOW_INSTANCE_NOT_FOUND)) }

        if (instance.tenant.id != tenantId) {
            throw NotFoundException(Error(code = ErrorCode.WORKFLOW_INSTANCE_NOT_FOUND))
        }
        return instance
    }

    fun search(
        ids: List<String>,
        workflowIds: List<Long>,
        participantUserId: Long?,
        status: WorkflowStatus?,
        startFrom: Date?,
        startTo: Date?,
        tenantId: Long,
        limit: Int,
        offset: Int,
        sortBy: WorkflowInstanceSortBy?,
        ascending: Boolean,
    ): List<WorkflowInstanceEntity> {
        val jql = StringBuilder("SELECT W FROM WorkflowInstanceEntity W")
        if (participantUserId != null) {
            jql.append(" JOIN W.participants P")
        }

        jql.append(" WHERE W.tenant.id = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND W.id IN :ids")
        }
        if (workflowIds.isNotEmpty()) {
            jql.append(" AND W.workflow.id IN :workflowIds")
        }
        if (participantUserId != null) {
            jql.append(" AND P.user.id = :participantUserId")
        }
        if (status != null) {
            jql.append(" AND W.status IN :status")
        }
        if (startFrom != null) {
            jql.append(" AND W.startAt >= :startFrom")
        }
        if (startTo != null) {
            jql.append(" AND W.startAt <= :startTo")
        }

        if (sortBy != null) {
            val column = when (sortBy) {
                WorkflowInstanceSortBy.ID -> "id"
                WorkflowInstanceSortBy.NAME -> "workflow.name"
            }
            jql.append(" ORDER BY W.$column")
            if (!ascending) {
                jql.append(" DESC")
            }
        }

        val query = em.createQuery(jql.toString(), WorkflowInstanceEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (workflowIds.isNotEmpty()) {
            query.setParameter("workflowIds", workflowIds)
        }
        if (participantUserId != null) {
            query.setParameter("participantUserId", participantUserId)
        }
        if (status != null) {
            query.setParameter("status", status)
        }
        if (startFrom != null) {
            query.setParameter("startFrom", startFrom)
        }
        if (startTo != null) {
            query.setParameter("startTo", startTo)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateWorkflowInstanceRequest, tenantId: Long): WorkflowInstanceEntity {
        val instance = createInstance(request, tenantId)
        createParticipants(request, instance, tenantId)
        createParameters(request, instance)
        return instance
    }

    @Transactional
    fun save(workflowInstance: WorkflowInstanceEntity) {
        instanceDao.save(workflowInstance)
    }

    @Transactional
    fun setState(name: String, value: String?, workflowInstance: WorkflowInstanceEntity): StateEntity? {
        val state = stateDao.findByNameAndInstance(name, workflowInstance)
        if (state == null) {
            if (!value.isNullOrEmpty()) {
                return stateDao.save(
                    StateEntity(
                        name = name,
                        value = value,
                        instance = workflowInstance,
                    )
                )
            }
        } else {
            if (value.isNullOrEmpty()) {
                stateDao.delete(state)
            } else {
                state.value = value
                return stateDao.save(state)
            }
        }
        return null
    }

    private fun createInstance(
        request: CreateWorkflowInstanceRequest,
        tenantId: Long
    ): WorkflowInstanceEntity {
        val workflow = workflowService.get(request.workflowId, tenantId)
        val instance = instanceDao.save(
            WorkflowInstanceEntity(
                id = UUID.randomUUID().toString(),
                tenant = workflow.tenant,
                workflow = workflow,
                status = WorkflowStatus.NEW,
                approver = request.approverUserId?.let { userId -> userService.get(userId, tenantId) },
                startAt = request.startAt,
                dueAt = request.dueAt,
            )
        )
        LOGGER.debug(">>> WorkflowInstance created: ${instance.id}")
        return instance
    }

    private fun createParticipants(
        request: CreateWorkflowInstanceRequest,
        instance: WorkflowInstanceEntity,
        tenantId: Long
    ): List<ParticipantEntity> {
        // Roles
        val roleIds = request.participants.map { participant -> participant.roleId }.toSet()
        if (roleIds.isEmpty()) {
            return emptyList()
        }
        val roleMap = roleService.getAll(roleIds.toList(), tenantId).associateBy { role -> role.id }

        // Users
        val userIds = request.participants.map { participant -> participant.userId }.toSet()
        val userMap = userService.getAll(userIds.toList(), tenantId).associateBy { user -> user.id }

        // Participants
        val participants = request.participants
            .map { participant ->
                val role = roleMap[participant.roleId]!!
                val user = userMap[participant.userId]!!
                LOGGER.debug(">>> ${instance.id} - Adding Participant User[${user.id}] as Role[${role.name}]")
                ParticipantEntity(
                    instance = instance,
                    role = role,
                    user = user
                )
            }

        if (participants.isNotEmpty()) {
            participantDao.saveAll(participants)
        }
        return participants
    }

    private fun createParameters(
        request: CreateWorkflowInstanceRequest,
        instance: WorkflowInstanceEntity,
    ): List<ParameterEntity> {
        val parameters = request.parameters
            .map { entry ->
                LOGGER.debug(">>> ${instance.id} - Adding Parameter[${entry.key}=${entry.value}]")
                ParameterEntity(
                    instance = instance,
                    name = entry.key,
                    value = entry.value
                )
            }

        if (parameters.isNotEmpty()) {
            parameterDao.saveAll(parameters)
        }
        return parameters
    }
}
