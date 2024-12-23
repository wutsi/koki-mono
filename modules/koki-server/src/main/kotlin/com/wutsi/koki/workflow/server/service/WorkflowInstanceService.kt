package com.wutsi.koki.workflow.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.Participant
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ParticipantRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.domain.ParticipantEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
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
    private val workflowService: WorkflowService,
    private val userService: UserService,
    private val roleService: RoleService,
    private val formDataService: FormDataService,
    private val securityService: SecurityService,
    private val activityService: ActivityService,
    private val taskDispatcher: WorkflowTaskDispatcher,
    private val em: EntityManager,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowInstanceService::class.java)
    }

    fun get(id: String, tenantId: Long): WorkflowInstanceEntity {
        val instance = instanceDao.findById(id)
            .orElseThrow { NotFoundException(Error(code = ErrorCode.WORKFLOW_INSTANCE_NOT_FOUND)) }

        if (instance.tenantId != tenantId) {
            throw NotFoundException(Error(code = ErrorCode.WORKFLOW_INSTANCE_NOT_FOUND))
        }
        return instance
    }

    fun search(
        ids: List<String>,
        workflowIds: List<Long>,
        participantUserIds: List<Long> = emptyList(),
        participantRoleIds: List<Long> = emptyList(),
        createdById: Long? = null,
        status: List<WorkflowStatus> = emptyList(),
        startFrom: Date?,
        startTo: Date?,
        tenantId: Long,
        limit: Int,
        offset: Int,
    ): List<WorkflowInstanceEntity> {
        val jql = StringBuilder("SELECT W FROM WorkflowInstanceEntity W")
        if (participantUserIds.isNotEmpty() || participantRoleIds.isNotEmpty()) {
            jql.append(" JOIN W.participants P")
        }

        jql.append(" WHERE W.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND W.id IN :ids")
        }
        if (workflowIds.isNotEmpty()) {
            jql.append(" AND W.workflowId IN :workflowIds")
        }
        if (participantUserIds.isNotEmpty()) {
            jql.append(" AND P.userId IN :participantUserIds")
        }
        if (participantRoleIds.isNotEmpty()) {
            jql.append(" AND P.roleId IN :participantRoleIds")
        }
        if (createdById != null) {
            jql.append(" AND W.createdById IN :createdById")
        }
        if (status.isNotEmpty()) {
            jql.append(" AND W.status IN :status")
        }
        if (startFrom != null) {
            jql.append(" AND W.startAt >= :startFrom")
        }
        if (startTo != null) {
            jql.append(" AND W.startAt <= :startTo")
        }
        jql.append(" ORDER BY W.createdAt DESC")

        val query = em.createQuery(jql.toString(), WorkflowInstanceEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (workflowIds.isNotEmpty()) {
            query.setParameter("workflowIds", workflowIds)
        }
        if (participantUserIds.isNotEmpty()) {
            query.setParameter("participantUserIds", participantUserIds)
        }
        if (participantRoleIds.isNotEmpty()) {
            query.setParameter("participantRoleIds", participantRoleIds)
        }
        if (createdById != null) {
            query.setParameter("createdById", createdById)
        }
        if (status.isNotEmpty()) {
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
    fun create(event: FormSubmittedEvent): List<WorkflowInstanceEntity> {
        val tenantId = event.tenantId
        val workflowIds = activityService.search(
            tenantId = tenantId,
            active = true,
            type = ActivityType.START,
            formIds = listOf(event.formId),
            limit = Integer.MAX_VALUE
        ).map { activity -> activity.workflowId }.toSet()
        if (workflowIds.isEmpty()) {
            return emptyList()
        }

        val workflows = workflowService.search(
            ids = workflowIds.toList(),
            active = true,
            limit = workflowIds.size,
            tenantId = tenantId,
        )

        return workflows.mapNotNull { workflow ->
            try {
                val roleIds = workflow.activities.mapNotNull { activity -> activity.roleId }.toSet()
                create(workflow, roleIds, event)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to create workflow instance", ex)
                null
            }
        }
    }

    private fun create(
        workflow: WorkflowEntity,
        roleIds: Set<Long>,
        event: FormSubmittedEvent,
    ): WorkflowInstanceEntity {
        val workflowInstance = create(
            tenantId = workflow.tenantId,
            userId = event.userId,
            request = CreateWorkflowInstanceRequest(
                workflowId = workflow.id!!,
                startAt = Date(),
                participants = roleIds.mapNotNull { roleId -> toParticipant(roleId, event.tenantId) },
                approverUserId = workflow.approverRoleId?.let { roleId ->
                    taskDispatcher.dispatch(roleId, event.tenantId)?.id
                },
            ),
        )
        formDataService.linkWithWorkflowInstanceId(
            id = event.formDataId,
            workflowInstanceId = workflowInstance.id!!,
            tenantId = event.tenantId,
        )
        return workflowInstance
    }

    private fun toParticipant(roleId: Long, tenantId: Long): Participant? {
        return taskDispatcher.dispatch(roleId, tenantId)
            ?.let { user -> Participant(roleId = roleId, userId = (user.id ?: -1)) }
    }

    @Transactional
    fun create(request: CreateWorkflowInstanceRequest, tenantId: Long, userId: Long? = null): WorkflowInstanceEntity {
        val instance = createInstance(request, tenantId, userId)
        createParticipants(request, instance, tenantId)

        workflowService.onCreated(instance)
        return instance
    }

    @Transactional
    fun save(workflowInstance: WorkflowInstanceEntity): WorkflowInstanceEntity {
        workflowInstance.modifiedAt = Date()
        return instanceDao.save(workflowInstance)
    }

    @Transactional
    @Suppress("UNCHECKED_CAST")
    fun mergeState(data: Map<String, Any>, workflowInstance: WorkflowInstanceEntity) {
        // Merge
        var merged = mutableMapOf<String, Any>()
        workflowInstance.state?.let { state ->
            merged.putAll(
                objectMapper.readValue(state, Map::class.java) as Map<String, Any>
            )
        }
        merged.putAll(data)

        // Update the state - remove empty values
        workflowInstance.state = objectMapper.writeValueAsString(
            merged.filter { entry ->
                if (entry.value is Collection<*>) {
                    (entry.value as Collection<*>).isNotEmpty()
                } else {
                    entry.value.toString().isNotEmpty()
                }
            }
        )
        save(workflowInstance)
    }

    private fun createInstance(
        request: CreateWorkflowInstanceRequest,
        tenantId: Long,
        userId: Long?,
    ): WorkflowInstanceEntity {
        val workflow = workflowService.get(request.workflowId, tenantId)
        val instance = instanceDao.save(
            WorkflowInstanceEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                workflowId = workflow.id!!,
                createdById = (userId ?: securityService.getCurrentUserIdOrNull()),
                title = request.title,
                status = WorkflowStatus.NEW,
                approverId = request.approverUserId,
                startAt = request.startAt,
                dueAt = request.dueAt,
                parameters = objectMapper.writeValueAsString(
                    request.parameters.filter { entry -> entry.value.isNotEmpty() }
                )
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
                    workflowInstanceId = instance.id!!,
                    roleId = participant.roleId,
                    userId = participant.userId
                )
            }

        if (participants.isNotEmpty()) {
            participantDao.saveAll(participants)
        }
        return participants
    }
}
