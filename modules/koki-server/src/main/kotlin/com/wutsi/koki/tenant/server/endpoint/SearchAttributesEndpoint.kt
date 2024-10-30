package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.common.service.TenantIdProvider
import com.wutsi.koki.tenant.dto.SearchAttributeResponse
import com.wutsi.koki.tenant.server.mapper.AttributeMapper
import com.wutsi.koki.tenant.server.service.AttributeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchAttributesEndpoint(
    private val service: AttributeService,
    private val mapper: AttributeMapper,
    private val tenantIdProvider: TenantIdProvider
) {
    @GetMapping("/v1/attributes")
    fun get(
        @RequestParam(required = false) name: List<String> = emptyList()
    ): SearchAttributeResponse =
        SearchAttributeResponse(
            attributes = service.search(tenantIdProvider.get(), name)
                .map { attr -> mapper.toAttribute(attr) }
        )
}
