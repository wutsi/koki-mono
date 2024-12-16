package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.event.ExternalEvent
import com.wutsi.koki.workflow.dto.ReceiveExternalEventRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ReceiveExternalEventEndpoint(
    private val publisher: EventPublisher
) {
    @PostMapping("/v1/workflow-instances/{id}/events")
    fun publish(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @RequestBody @Valid request: ReceiveExternalEventRequest,
    ) {
        publisher.publish(
            ExternalEvent(
                tenantId = tenantId,
                workflowInstanceId = id,
                name = request.name,
                data = request.data,
                timestamp = System.currentTimeMillis(),
            )
        )
    }
}
