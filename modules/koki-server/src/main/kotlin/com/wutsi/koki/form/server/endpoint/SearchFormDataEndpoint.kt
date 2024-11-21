package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.form.server.mapper.FormMapper
import com.wutsi.koki.form.server.service.FormService
import com.wutsi.koki.workflow.dto.FormSortBy
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchFormEndpoint(
    private val service: FormService,
    private val mapper: FormMapper,
) {
    @GetMapping("/v1/forms")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "sort-by") sortBy: FormSortBy? = null,
        @RequestParam(required = false, name = "asc") ascending: Boolean = true,
    ): SearchFormResponse {
        val forms = service.search(
            ids = ids,
            tenantId = tenantId,
            active = active,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
            ascending = ascending,
        )
        return SearchFormResponse(
            forms = forms.map { form -> mapper.toFormSummary(form) }
        )
    }
}
