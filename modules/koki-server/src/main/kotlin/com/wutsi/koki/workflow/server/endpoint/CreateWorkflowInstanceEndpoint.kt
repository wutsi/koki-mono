package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.dto.ParameterType
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceResponse
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
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
        checkParameters(request, workflow)
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
