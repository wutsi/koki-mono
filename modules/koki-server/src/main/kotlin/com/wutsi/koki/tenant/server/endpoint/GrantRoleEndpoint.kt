package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.GrantRoleRequest
import com.wutsi.koki.tenant.server.service.RoleService
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
class GrantRoleEndpoint(
    private val service: UserService,
    private val roleService: RoleService,
) {
    @PostMapping("/v1/users/{id}/roles")
    fun grant(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @RequestBody @Valid request: GrantRoleRequest
    ) {
        val role = roleService.get(request.roleId, tenantId)
        service.grant(id, role, tenantId)
    }
}
