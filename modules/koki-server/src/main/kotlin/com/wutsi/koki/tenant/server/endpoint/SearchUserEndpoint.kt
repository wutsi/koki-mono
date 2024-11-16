package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.SearchUserResponse
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
        @RequestParam(required = false, name = "q") keyword: String = "",
        @RequestParam(required = false) id: List<Long> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "sort-by") sortBy: String? = null,
        @RequestParam(required = false, name = "asc") ascending: Boolean = true,
    ): SearchUserResponse {
        val users = service.search(keyword, id, tenantId, limit, offset, sortBy, ascending)
        return SearchUserResponse(
            users = users.map { user -> mapper.toUserSummary(user) }
        )
    }
}
