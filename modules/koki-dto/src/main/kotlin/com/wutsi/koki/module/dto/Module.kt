package com.wutsi.koki.module.dto

import com.wutsi.koki.common.dto.ObjectType

data class Module(
    val id: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val homeUrl: String? = null,
    val tabUrl: String? = null,
    val settingsUrl: String? = null,
    val objectType: ObjectType = ObjectType.UNKNOWN,
    val jsUrl: String? = null,
    val cssUrl: String? = null,
)
