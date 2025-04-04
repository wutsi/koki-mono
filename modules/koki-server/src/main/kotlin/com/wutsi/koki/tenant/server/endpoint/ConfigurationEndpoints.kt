package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SaveConfigurationResponse
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import com.wutsi.koki.tenant.server.mapper.ConfigurationMapper
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/configurations")
class ConfigurationEndpoints(
    private val service: ConfigurationService,
    private val mapper: ConfigurationMapper,
) {
    @PostMapping
    fun save(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody request: SaveConfigurationRequest
    ): SaveConfigurationResponse {
        return service.save(request, tenantId)
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "name") names: List<String> = emptyList(),
        @RequestParam(required = false, name = "q") keyword: String? = null,
    ): SearchConfigurationResponse {
        val configurations = service.search(
            tenantId = tenantId,
            names = names,
            keyword = keyword,
        )

        return SearchConfigurationResponse(
            configurations = configurations.map { config -> mapper.toConfiguration(config) }
        )
    }
}
