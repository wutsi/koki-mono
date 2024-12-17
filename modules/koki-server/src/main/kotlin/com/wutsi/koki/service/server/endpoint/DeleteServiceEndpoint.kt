package com.wutsi.koki.service.server.endpoint

import com.wutsi.koki.service.server.service.ServiceService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class DeleteServiceEndpoint(
    private val service: ServiceService
) {
    @DeleteMapping("/v1/services/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String
    ) {
        service.delete(id, tenantId)
    }
}
