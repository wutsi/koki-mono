package com.wutsi.koki.tenant.dto

import java.util.Date

@Deprecated("")
data class AttributeNVP(
    val id: Long = -1,
    val name: String = "",
    val value: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
