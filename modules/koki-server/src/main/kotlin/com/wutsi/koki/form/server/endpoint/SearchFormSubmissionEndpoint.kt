package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.SearchFormSubmissionResponse
import com.wutsi.koki.form.server.mapper.FormSubmissionMapper
import com.wutsi.koki.form.server.service.FormSubmissionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchFormSubmissionEndpoint(
    private val service: FormSubmissionService,
    private val mapper: FormSubmissionMapper,
) {
    @GetMapping("/v1/form-submissions")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "form-id") formIds: List<String> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchFormSubmissionResponse {
        val formSubmissions = service.search(
            tenantId = tenantId,
            formIds = formIds,
            limit = limit,
            offset = offset,
        )
        return SearchFormSubmissionResponse(
            formSubmissions = formSubmissions.map { submission -> mapper.toFormSubmissionSummary(submission) }
        )
    }
}
