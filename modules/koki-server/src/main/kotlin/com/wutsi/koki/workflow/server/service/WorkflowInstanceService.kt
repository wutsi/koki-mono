package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ParameterRepository
import com.wutsi.koki.workflow.server.dao.ParticipantRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.domain.ParameterEntity
import com.wutsi.koki.workflow.server.domain.ParticipantEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class WorkflowInstanceService(
    private val instanceDao: WorkflowInstanceRepository,
    private val participantDao: ParticipantRepository,
    private val parameterDao: ParameterRepository,
    private val workflowService: WorkflowService,
    private val userService: UserService,
    private val roleService: RoleService,
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

    @Transactional
    fun create(request: CreateWorkflowInstanceRequest, tenantId: Long): WorkflowInstanceEntity {
        val instance = createInstance(request, tenantId)
        createParticipants(request, instance, tenantId)
        createParameters(request, instance)
        return instance
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
