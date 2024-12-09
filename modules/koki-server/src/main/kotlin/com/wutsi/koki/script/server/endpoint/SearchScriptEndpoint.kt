package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.script.dto.ScriptSortBy
import com.wutsi.koki.script.dto.SearchScriptResponse
import com.wutsi.koki.script.server.mapper.ScriptMapper
import com.wutsi.koki.script.server.service.ScriptService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchScriptEndpoint(
    private val service: ScriptService,
    private val mapper: ScriptMapper,
) {
    @GetMapping("/v1/scripts")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "name") names: List<String> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "sort-by") sortBy: ScriptSortBy? = null,
        @RequestParam(required = false, name = "asc") ascending: Boolean = true,
    ): SearchScriptResponse {
        val scripts = service.search(
            tenantId = tenantId,
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
            ascending = ascending
        )
        return SearchScriptResponse(
            scripts = scripts.map { script -> mapper.toScriptSummary(script) }
        )
    }
}
