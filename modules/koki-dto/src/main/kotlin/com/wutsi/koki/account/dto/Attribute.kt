package com.wutsi.koki.account.dto

import java.util.Collections.emptyList
import java.util.Date

data class Attribute(
    val id: Long = -1,
    val name: String = "",
    val type: AttributeType = AttributeType.UNKNOWN,
    val required: Boolean = false,
    val active: Boolean = false,
    val label: String? = null,
    val description: String? = null,
    val choices: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
