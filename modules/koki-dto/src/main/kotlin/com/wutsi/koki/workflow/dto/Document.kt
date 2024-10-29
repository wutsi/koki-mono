package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class Document(
    val id: Long = -1,
    val name: String = "",
    val description: String = "",
    val active: Boolean = true,
    val createdAt: Date = Date(),
)
