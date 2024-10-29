package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class Attribute(
    val id: Long = -1,
    val name: String = "",
    val label: String = "",
    val description: String = "",
    val type: AttributeType = AttributeType.UNKNOWN,
    val choices: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val active: Boolean = true,
)
