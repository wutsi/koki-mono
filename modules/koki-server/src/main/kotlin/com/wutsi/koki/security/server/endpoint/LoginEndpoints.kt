package com.wutsi.koki.security.server.endpoint

import com.wutsi.koki.security.dto.LoginRequest
import com.wutsi.koki.security.dto.LoginResponse
import com.wutsi.koki.security.server.service.AuthenticationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class LoginEndpoints(private val service: AuthenticationService) {
    @PostMapping("/v1/auth/login")
    fun login(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: LoginRequest
    ): LoginResponse {
        val accessToken = service.authenticate(request, tenantId)
        return LoginResponse(accessToken)
    }
}
