package com.wutsi.koki.module.dto

data class Permission(
    val id: Long = -1,
    val moduleId: Long = -1,
    val name: String = "",
    val description: String? = null,
)
