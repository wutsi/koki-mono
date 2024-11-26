package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.SetActivityInstanceApproverRequest
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SetActivityInstanceApproverEndpoint(
    private val service: ActivityInstanceService,
) {
    @PostMapping("/v1/activity-instances/approver")
    fun complete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: SetActivityInstanceApproverRequest
    ) {
        service.setApprover(request, tenantId)
    }
}
