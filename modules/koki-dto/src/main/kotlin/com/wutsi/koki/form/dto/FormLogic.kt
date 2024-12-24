package com.wutsi.koki.form.dto

data class FormLogic(
    val action: FormAction = FormAction.UNKNOWN,
    val expression: String = "",
)
