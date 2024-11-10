package com.wutsi.koki.form.dto

import jakarta.validation.constraints.NotEmpty

data class SaveFormDataRequest(
    @get:NotEmpty val data: Map<String, String> = emptyMap()
)
