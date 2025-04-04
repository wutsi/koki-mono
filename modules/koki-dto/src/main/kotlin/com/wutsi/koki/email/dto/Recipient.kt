package com.wutsi.koki.email.dto

import com.wutsi.koki.common.dto.ObjectType
import jakarta.validation.constraints.NotNull

data class Recipient(
    val id: Long? = null,
    val type: ObjectType = ObjectType.UNKNOWN,
    @get:NotNull val email: String = "",
    val displayName: String? = null,
    val language: String? = null,
)
