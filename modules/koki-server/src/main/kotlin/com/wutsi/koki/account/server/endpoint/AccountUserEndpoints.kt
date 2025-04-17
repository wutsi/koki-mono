package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.account.dto.CreateAccountUserRequest
import com.wutsi.koki.account.dto.CreateAccountUserResponse
import com.wutsi.koki.account.dto.GetAccountUserResponse
import com.wutsi.koki.account.dto.UpdateAccountUserRequest
import com.wutsi.koki.account.server.mapper.AccountUserMapper
import com.wutsi.koki.account.server.service.AccountUserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/account-users")
class AccountUserEndpoints(
    private val service: AccountUserService,
    private val mapper: AccountUserMapper,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateAccountUserRequest,
    ): CreateAccountUserResponse {
        val user = service.create(request, tenantId)
        return CreateAccountUserResponse(
            accountUserId = user.id ?: -1
        )
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateAccountUserRequest,
    ) {
        service.update(id, request, tenantId)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetAccountUserResponse {
        val user = service.get(id, tenantId)
        return GetAccountUserResponse(
            accountUser = mapper.toAccountUser(user)
        )
    }
}
