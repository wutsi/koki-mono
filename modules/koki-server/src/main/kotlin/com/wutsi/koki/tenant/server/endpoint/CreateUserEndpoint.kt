package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.GetUserResponse
import com.wutsi.koki.tenant.server.mapper.UserMapper
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetUserEndpoint(
    private val service: UserService,
    private val mapper: UserMapper,
) {
    @GetMapping("/v1/users/{id}")
    fun get(
        @PathVariable id: Long,
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
    ): GetUserResponse {
        return GetUserResponse(
            user = mapper.toUser(
                entity = service.get(id, tenantId)
            )
        )
    }
}
