package com.wutsi.koki.message.dto

import jakarta.validation.constraints.NotEmpty

data class CreateMessageRequest(
    @get:NotEmpty val name: String = "",
    @get:NotEmpty val subject: String = "",
    @get:NotEmpty val body: String = "",
    val active: Boolean = true,
    var description: String = "",
)
