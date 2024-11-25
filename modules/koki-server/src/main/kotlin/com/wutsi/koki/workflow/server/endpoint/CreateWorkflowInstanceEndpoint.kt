package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.dto.ParameterType
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceResponse
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import com.wutsi.koki.workflow.server.service.WorkflowService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CreateWorkflowInstanceEndpoint(
    private val service: WorkflowInstanceService,
    private val workflowService: WorkflowService,
    private val activityService: ActivityService,
    private val roleService: RoleService,
) {
    @PostMapping("/v1/workflow-instances")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: CreateWorkflowInstanceRequest
    ): CreateWorkflowInstanceResponse {
        val workflow = workflowService.get(request.workflowId, tenantId)
        validate(request, workflow)

        val instance = service.create(request, tenantId)
        return CreateWorkflowInstanceResponse(
            workflowInstanceId = instance.id ?: ""
        )
    }

    private fun validate(request: CreateWorkflowInstanceRequest, workflow: WorkflowEntity) {
        checkWorkflowStatus(workflow)

        val activities = activityService.getByWorkflow(workflow)
        checkParameters(request, workflow)
        checkApprover(request, activities)
        checkParticipants(request, activities)
    }

    private fun checkWorkflowStatus(workflow: WorkflowEntity) {
        if (!workflow.active) {
            throw badRequest(ErrorCode.WORKFLOW_NOT_ACTIVE)
        }
    }

    private fun checkParameters(
        request: CreateWorkflowInstanceRequest,
        workflow: WorkflowEntity,
    ) {
        // Missing parameters
        val parameters = workflow.parameterAsList()
        if (parameters.isEmpty()) {
            return
        }
        parameters.forEach { param ->
            if (!request.parameters.containsKey(param)) {
                throw badRequest(ErrorCode.WORKFLOW_INSTANCE_PARAMETER_MISSING, param)
            }
        }

        // Bad parameters
        request.parameters.keys.forEach { param ->
            if (!parameters.contains(param)) {
                throw badRequest(ErrorCode.WORKFLOW_INSTANCE_PARAMETER_NOT_VALID, param)
            }
        }
    }

    private fun checkApprover(
        request: CreateWorkflowInstanceRequest,
        activities: List<ActivityEntity>
    ) {
        val requiresApprover = activities.find { activity -> activity.requiresApproval } != null
        if (requiresApprover && request.approverUserId == null) {
            throw badRequest(ErrorCode.WORKFLOW_INSTANCE_APPROVER_MISSING)
        }
    }

    private fun checkParticipants(
        request: CreateWorkflowInstanceRequest,
        activities: List<ActivityEntity>
    ) {
        val roleIds = activities.mapNotNull { activity -> activity.roleId }.distinct()
        if (roleIds.isEmpty()) {
            return
        }

        // Invalid participants
        request.participants.forEach { participant ->
            if (!roleIds.contains(participant.roleId)) {
                throw badRequest(ErrorCode.WORKFLOW_INSTANCE_PARTICIPANT_NOT_VALID, participant.roleId.toString())
            }
        }
    }

    private fun badRequest(code: String, value: String? = null): BadRequestException {
        return BadRequestException(
            error = Error(
                code = code,
                parameter = value?.let {
                    Parameter(
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                        value = value
                    )
                }
            )
        )
    }
}
