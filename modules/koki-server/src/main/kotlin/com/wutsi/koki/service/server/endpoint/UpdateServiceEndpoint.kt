package com.wutsi.koki.service.server.endpoint

import com.wutsi.koki.service.dto.UpdateServiceRequest
import com.wutsi.koki.service.server.service.ServiceService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UpdateServiceEndpoint(
    private val service: ServiceService
) {
    @PostMapping("/v1/services/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateServiceRequest,
    ) {
        service.update(id, request, tenantId)
    }
}
