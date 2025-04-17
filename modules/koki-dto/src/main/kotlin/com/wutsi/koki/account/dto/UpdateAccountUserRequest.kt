package com.wutsi.koki.account.dto

import com.wutsi.koki.tenant.dto.UserStatus
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UpdateAccountUserRequest(
    @get:NotEmpty @get:Size(max = 100) val username: String = "",
    val status: UserStatus = UserStatus.NEW,
)
