package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.server.service.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UpdateUserEndpoint(private val service: UserService) {
    @PostMapping("/v1/users/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @RequestBody @Valid request: UpdateUserRequest
    ) {
        service.update(id, request, tenantId)
    }
}
