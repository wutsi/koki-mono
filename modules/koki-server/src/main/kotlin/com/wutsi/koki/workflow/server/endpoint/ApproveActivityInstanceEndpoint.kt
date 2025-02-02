package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.workflow.dto.ApproveActivityInstanceRequest
import com.wutsi.koki.workflow.dto.ApproveActivityInstanceResponse
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ApproveActivityInstanceEndpoint(
    private val engine: WorkflowEngine,
    private val securityService: SecurityService,
) {
    @PostMapping("/v1/activity-instances/{id}/approvals")
    fun complete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @RequestBody @Valid request: ApproveActivityInstanceRequest
    ): ApproveActivityInstanceResponse {
        val approverId = securityService.getCurrentUserId()
        val approval = engine.approve(id, request.status, approverId, request.comment, tenantId)
        return ApproveActivityInstanceResponse(approval.id ?: -1)
    }
}
