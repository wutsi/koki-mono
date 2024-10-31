package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SaveConfigurationResponse
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SaveConfigurationEndpoint(private val service: ConfigurationService) {
    @PostMapping("/v1/configurations")
    fun save(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody request: SaveConfigurationRequest
    ): SaveConfigurationResponse {
        return service.save(request, tenantId)
    }
}
