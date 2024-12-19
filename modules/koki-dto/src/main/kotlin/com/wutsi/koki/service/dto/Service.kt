package com.wutsi.koki.service.dto

import java.util.Date

data class Service(
    val id: String = "",
    val name: String = "",
    val title: String? = null,
    val description: String? = null,
    val baseUrl: String = "",
    val authorizationType: AuthorizationType = AuthorizationType.UNKNOWN,
    val username: String? = null,
    val password: String? = null,
    val apiKey: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
