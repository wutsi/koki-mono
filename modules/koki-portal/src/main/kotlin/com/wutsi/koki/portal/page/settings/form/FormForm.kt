package com.wutsi.koki.portal.page.settings.form

data class FormForm(
    val name: String = "",
    val title: String = "",
    val description: String = "",
    val elements: String = "[]",
    val active: Boolean = true,
)
