package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.SearchAttributeResponse
import com.wutsi.koki.tenant.server.mapper.AttributeMapper
import com.wutsi.koki.tenant.server.service.AttributeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchAttributeEndpoint(
    private val service: AttributeService,
    private val mapper: AttributeMapper,
) {
    @GetMapping("/v1/attributes")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false) name: List<String> = emptyList()
    ): SearchAttributeResponse =
        SearchAttributeResponse(
            attributes = service.search(tenantId, name)
                .map { attr -> mapper.toAttribute(attr) }
        )
}
