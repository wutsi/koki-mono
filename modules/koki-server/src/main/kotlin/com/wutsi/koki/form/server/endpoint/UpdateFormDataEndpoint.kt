package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.form.event.FormUpdatedEvent
import com.wutsi.koki.form.server.service.FormDataService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UpdateFormDataEndpoint(
    private val service: FormDataService,
    private val eventPublisher: EventPublisher,
) {
    @PostMapping("/v1/form-data/{id}")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @RequestBody @Valid request: UpdateFormDataRequest
    ) {
        val formData = service.update(id, request, tenantId)

        eventPublisher.publish(
            FormUpdatedEvent(
                formId = formData.formId,
                formDataId = formData.id!!,
                activityInstanceId = request.activityInstanceId
            )
        )
    }
}
