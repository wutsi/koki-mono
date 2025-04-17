package com.wutsi.koki.account.dto

import com.wutsi.koki.tenant.dto.UserStatus
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateAccountUserRequest(
    @get:NotEmpty @get:Size(max = 100) val username: String = "",
    @get:NotEmpty val password: String = "",
    val accountId: Long = -1,
    val status: UserStatus = UserStatus.NEW,
)
