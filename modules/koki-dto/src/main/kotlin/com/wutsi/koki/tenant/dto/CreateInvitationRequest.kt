package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateInvitationRequest(
    @get:Email @get:Size(max = 255) val email: String = "",
    @get:NotEmpty val displayName: String = "",
    val type: InvitationType = InvitationType.UNKNOWN,
    val language: String? = null,
)
