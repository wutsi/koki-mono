package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.FormDataStatus
import com.wutsi.koki.form.dto.SearchFormDataResponse
import com.wutsi.koki.form.server.mapper.FormDataMapper
import com.wutsi.koki.form.server.service.FormDataService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchFormDataEndpoint(
    private val service: FormDataService,
    private val mapper: FormDataMapper,
) {
    @GetMapping("/v1/form-data")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "form-id") formIds: List<String> = emptyList(),
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceIds: List<String> = emptyList(),
        @RequestParam(required = false) status: FormDataStatus? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchFormDataResponse {
        val forms = service.search(
            tenantId = tenantId,
            ids = ids,
            formIds = formIds,
            workflowInstanceIds = workflowInstanceIds,
            status = status,
            limit = limit,
            offset = offset,
        )
        return SearchFormDataResponse(
            formData = forms.map { form -> mapper.toFormDataSummary(form) }
        )
    }
}
