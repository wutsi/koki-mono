package com.wutsi.koki.form.dto

import java.util.Date

data class Form(
    val id: Long = -1,
    val code: String = "",
    val name: String = "",
    var description: String? = null,
    var active: Boolean = true,
    val createdAt: Date = Date(),
    val createdById: Long? = null,
    var modifiedAt: Date = Date(),
    val modifiedById: Long? = null,
)
