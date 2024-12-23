package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.SubmitFormDataResponse
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SubmitFormDataEndpoint(
    private val service: FormDataService,
    private val eventPublisher: EventPublisher,
    private val securityService: SecurityService,
) {
    @PostMapping("/v1/form-data")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: SubmitFormDataRequest,
    ): SubmitFormDataResponse {
        val formData = service.submit(request, tenantId)
        val response = SubmitFormDataResponse(formData.id!!)

        eventPublisher.publish(
            FormSubmittedEvent(
                tenantId = tenantId,
                formId = request.formId,
                formDataId = formData.id,
                activityInstanceId = request.activityInstanceId,
                userId = securityService.getCurrentUserIdOrNull(),
            )
        )
        return response
    }
}
