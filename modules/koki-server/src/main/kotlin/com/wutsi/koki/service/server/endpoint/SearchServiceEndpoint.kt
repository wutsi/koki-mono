package com.wutsi.koki.service.server.endpoint

import com.wutsi.koki.service.dto.GetServiceResponse
import com.wutsi.koki.service.server.mapper.ServiceMapper
import com.wutsi.koki.service.server.service.ServiceService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetServiceEndpoint(
    private val service: ServiceService,
    private val mapper: ServiceMapper,
) {
    @GetMapping("/v1/services/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String
    ): GetServiceResponse {
        val service = service.get(id, tenantId)
        return GetServiceResponse(
            service = mapper.toService(service)
        )
    }
}
