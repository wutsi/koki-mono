package com.wutsi.koki.module.dto

data class Module(
    val id: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val homeUrl: String? = null,
    val tabUrl: String? = null,
    val settingsUrl: String? = null,
)
