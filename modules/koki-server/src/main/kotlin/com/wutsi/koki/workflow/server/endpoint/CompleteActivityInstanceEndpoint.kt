package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.CompleteActivityInstanceRequest
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CompleteActivityInstanceEndpoint(
    private val service: ActivityInstanceService,
    private val engine: WorkflowEngine,
) {
    @PostMapping("/v1/activity-instances/{id}/complete")
    fun complete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @RequestBody @Valid request: CompleteActivityInstanceRequest
    ) {
        val activityInstance = service.get(id, tenantId)
        engine.done(activityInstance, request.state)
    }
}
