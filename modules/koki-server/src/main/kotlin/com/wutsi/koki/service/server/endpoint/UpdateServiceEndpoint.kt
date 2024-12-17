package com.wutsi.koki.service.server.endpoint

import com.wutsi.koki.service.dto.CreateServiceRequest
import com.wutsi.koki.service.dto.CreateServiceResponse
import com.wutsi.koki.service.server.service.ServiceService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CreateServiceEndpoint(
    private val service: ServiceService
) {
    @PostMapping("/v1/services")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateServiceRequest,
    ): CreateServiceResponse {
        val service = service.create(request, tenantId)
        return CreateServiceResponse(service.id!!)
    }
}
