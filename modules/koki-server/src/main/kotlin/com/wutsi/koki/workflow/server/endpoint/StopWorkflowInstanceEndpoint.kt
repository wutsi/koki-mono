package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class StopWorkflowInstanceEndpoint(
    private val engine: WorkflowEngine,
) {
    @PostMapping("/v1/workflow-instances/{id}/stop")
    fun stop(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String
    ) {
        engine.done(id, tenantId)
    }
}
