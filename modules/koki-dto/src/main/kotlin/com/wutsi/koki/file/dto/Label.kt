package com.wutsi.koki.file.dto

import java.util.Date

data class Label(
    val id: Long = -1L,
    val displayName: String = "",
    val createdAt: Date = Date(),
)
