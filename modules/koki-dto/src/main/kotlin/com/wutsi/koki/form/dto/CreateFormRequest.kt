package com.wutsi.koki.form.dto

import com.wutsi.koki.common.dto.ObjectReference
import jakarta.validation.constraints.NotEmpty

data class CreateFormRequest(
    @get:NotEmpty() val name: String = "",
    var description: String? = null,
    var active: Boolean = true,
    val owner: ObjectReference? = null,
)
