package com.wutsi.koki.common.dto

import jakarta.validation.constraints.NotNull

data class ObjectReference(
    val id: Long = -1,
    @get:NotNull val type: ObjectType = ObjectType.UNKNOWN,
)
