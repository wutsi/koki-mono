package com.wutsi.koki.service.server.endpoint

import com.wutsi.koki.script.dto.ServiceSortBy
import com.wutsi.koki.service.dto.SearchServiceResponse
import com.wutsi.koki.service.server.mapper.ServiceMapper
import com.wutsi.koki.service.server.service.ServiceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchServiceEndpoint(
    private val service: ServiceService,
    private val mapper: ServiceMapper,
) {
    @GetMapping("/v1/services")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "name") names: List<String> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "sort-by") sortBy: ServiceSortBy? = null,
        @RequestParam(required = false, name = "asc") ascending: Boolean = true,
    ): SearchServiceResponse {
        val services = service.search(
            tenantId = tenantId,
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
            ascending = ascending,
        )
        return SearchServiceResponse(
            services = services.map { service -> mapper.toServiceSummary(service) }
        )
    }
}
