package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.CreateUserResponse
import com.wutsi.koki.tenant.dto.GetUserResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserType
import com.wutsi.koki.tenant.server.mapper.UserMapper
import com.wutsi.koki.tenant.server.service.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder

@RestController
@RequestMapping("/v1/users")
class UserEndpoints(
    private val service: UserService,
    private val mapper: UserMapper,
) {
    @GetMapping("/{id}")
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

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false) id: List<Long> = emptyList(),
        @RequestParam(required = false, name = "role-id") roleIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "permission") permissions: List<String> = emptyList(),
        @RequestParam(required = false) status: UserStatus? = null,
        @RequestParam(required = false) type: UserType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchUserResponse {
        val users = service.search(
            keyword = keyword,
            ids = id,
            roleIds = roleIds,
            tenantId = tenantId,
            status = status,
            type = type,
            permissions = permissions.map { permission -> URLDecoder.decode(permission, "utf-8") },
            limit = limit,
            offset = offset,
        )
        return SearchUserResponse(
            users = users.map { user -> mapper.toUserSummary(user) }
        )
    }

    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: CreateUserRequest
    ): CreateUserResponse {
        return CreateUserResponse(
            userId = service.create(request, tenantId).id ?: -1
        )
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @RequestBody @Valid request: UpdateUserRequest
    ) {
        service.update(id, request, tenantId)
    }
}
