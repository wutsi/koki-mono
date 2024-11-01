package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.workflow.dto.ImportWorkflowRequest
import com.wutsi.koki.workflow.dto.ImportWorkflowResponse
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.io.WorkflowImporter
import com.wutsi.koki.workflow.server.service.WorkflowService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ImportWorkflowEndpoint(
    private val importer: WorkflowImporter,
    private val service: WorkflowService,
    private val tenantService: TenantService,
) {
    @PostMapping("/v1/workflows")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: ImportWorkflowRequest
    ): ImportWorkflowResponse {
        val workflow = WorkflowEntity(
            tenant = tenantService.get(tenantId)
        )
        importer.import(workflow, request.workflow)
        return ImportWorkflowResponse(
            workflowId = workflow.id ?: -1
        )
    }

    @PostMapping("/v1/workflows/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @RequestBody @Valid request: ImportWorkflowRequest
    ): ImportWorkflowResponse {
        val workflow = service.get(id, tenantId)
        importer.import(workflow, request.workflow)
        return ImportWorkflowResponse(
            workflowId = workflow.id ?: -1
        )
    }
}
