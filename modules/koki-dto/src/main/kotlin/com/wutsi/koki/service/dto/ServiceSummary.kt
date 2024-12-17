package com.wutsi.koki.service.dto

import java.util.Date

data class ServiceSummary(
    val id: String = "",
    val name: String = "",
    val title: String? = null,
    val baseUrl: String = "",
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
