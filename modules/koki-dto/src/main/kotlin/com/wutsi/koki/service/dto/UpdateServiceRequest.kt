package com.wutsi.koki.service.dto

import jakarta.validation.constraints.NotEmpty

data class UpdateServiceRequest(
    @get:NotEmpty val name: String = "",
    @get:NotEmpty val baseUrl: String = "",
    val title: String? = null,
    val description: String? = null,
    val authenticationType: AuthenticationType = AuthenticationType.UNKNOWN,
    val username: String? = null,
    val password: String? = null,
    val apiKey: String? = null,
    val active: Boolean = true,
)
