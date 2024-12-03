package com.wutsi.koki.tenant.dto

import java.util.Date

data class Configuration(
    val name: String = "",
    val value: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
