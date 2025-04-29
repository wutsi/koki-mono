package com.wutsi.koki.account.dto

import com.wutsi.koki.tenant.dto.UserStatus
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateUserRequest(
    @get:NotEmpty @get:Size(max = 100) val username: String = "",
    @get:NotEmpty val password: String = "",
    val status: UserStatus = UserStatus.NEW,
)
