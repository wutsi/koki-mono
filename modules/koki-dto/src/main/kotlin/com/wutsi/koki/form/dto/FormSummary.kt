package com.wutsi.koki.form.dto

import java.util.Date

data class FormSummary(
    val id: Long = -1,
    val name: String = "",
    var active: Boolean = true,
    val createdAt: Date = Date(),
    val createdById: Long? = null,
    var modifiedAt: Date = Date(),
    val modifiedById: Long? = null,
    var deleted: Boolean = false,
    var deletedAt: Date? = null,
)
