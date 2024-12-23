package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.GetFormSubmissionResponse
import com.wutsi.koki.form.server.mapper.FormSubmissionMapper
import com.wutsi.koki.form.server.service.FormSubmissionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetFormSubmissionEndpoint(
    private val service: FormSubmissionService,
    private val mapper: FormSubmissionMapper,
) {
    @GetMapping("/v1/form-submissions/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String
    ): GetFormSubmissionResponse {
        val formSubmission = service.get(id, tenantId)
        return GetFormSubmissionResponse(
            formSubmission = mapper.toFormSubmission(formSubmission)
        )
    }
}
