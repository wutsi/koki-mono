package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.mapper.UserMapper
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchUserEndpoint(
    private val service: UserService,
    private val mapper: UserMapper,
) {
    @GetMapping("/v1/users")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false) id: List<Long> = emptyList(),
        @RequestParam(required = false, name = "role-id") roleId: List<Long> = emptyList(),
        @RequestParam(required = false) status: UserStatus? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchUserResponse {
        val users = service.search(
            keyword = keyword,
            ids = id,
            roleIds = roleId,
            tenantId = tenantId,
            status = status,
            limit = limit,
            offset = offset,
        )
        return SearchUserResponse(
            users = users.map { user -> mapper.toUserSummary(user) }
        )
    }
}
