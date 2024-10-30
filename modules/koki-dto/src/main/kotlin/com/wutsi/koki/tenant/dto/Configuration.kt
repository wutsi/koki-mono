package com.wutsi.koki.tenant.dto

import java.util.Date

data class Configuration(
    val id: Long = -1,
    val attribute: Attribute = Attribute(),
    val value: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
