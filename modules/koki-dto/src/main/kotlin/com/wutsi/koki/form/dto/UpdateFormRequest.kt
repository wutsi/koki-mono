package com.wutsi.koki.form.dto

import jakarta.validation.constraints.NotEmpty

data class UpdateFormRequest(
    @get:NotEmpty() val name: String = "",
    var description: String? = null,
    var active: Boolean = true,
)
