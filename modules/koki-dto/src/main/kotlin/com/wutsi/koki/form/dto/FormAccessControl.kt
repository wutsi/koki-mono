package com.wutsi.koki.form.dto

data class FormAccessControl(
    val viewerRoles: List<String>? = null,
    val editorRoles: List<String>? = null,
)
