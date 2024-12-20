package com.wutsi.koki.portal.service

data class ServiceForm(
    val name: String = "",
    val title: String = "",
    val description: String = "",
    val baseUrl: String = "",
    val authorizationType: String = "",
    val username: String? = null,
    val password: String? = null,
    val apiKey: String? = null,
    val active: Boolean = true,
)
