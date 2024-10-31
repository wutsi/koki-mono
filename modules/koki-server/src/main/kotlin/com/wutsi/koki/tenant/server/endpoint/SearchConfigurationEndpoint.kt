package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import com.wutsi.koki.tenant.server.mapper.ConfigurationMapper
import com.wutsi.koki.tenant.server.service.AttributeService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchConfigurationEndpoint(
    private val service: ConfigurationService,
    private val mapper: ConfigurationMapper,
    private val attributeService: AttributeService,
) {
    @GetMapping("/v1/configurations")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false) name: List<String> = emptyList()
    ): SearchConfigurationResponse {
        val attributeMap = attributeService.search(tenantId, name).associateBy { it.id }
        if (attributeMap.isEmpty()) {
            return SearchConfigurationResponse()
        }

        return SearchConfigurationResponse(
            configurations = service
                .search(attributeMap.values.toList())
                .mapNotNull { config ->
                    attributeMap[config.attribute.id]?.let { attr ->
                        mapper.toConfiguration(config, attr)
                    }
                }
        )
    }
}
