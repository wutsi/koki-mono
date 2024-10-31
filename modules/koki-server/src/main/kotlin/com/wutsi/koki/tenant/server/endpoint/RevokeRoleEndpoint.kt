package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class RevokeRoleEndpoint(
    private val service: UserService,
    private val roleService: RoleService,
) {
    @DeleteMapping("/v1/users/{id}/roles/{role-id}")
    fun revoke(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @PathVariable("role-id") roleId: Long,
    ) {
        val role = roleService.get(roleId, tenantId)
        service.revoke(id, role, tenantId)
    }
}
