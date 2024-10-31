package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.server.mapper.RoleMapper
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetUserRoleEndpoint(
    private val service: UserService,
    private val mapper: RoleMapper,
) {
    @GetMapping("/v1/users/{id}/roles")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): SearchRoleResponse {
        val user = service.get(id, tenantId)
        return SearchRoleResponse(
            roles = user.roles.map { role -> mapper.toRole(role) }
        )
    }
}
