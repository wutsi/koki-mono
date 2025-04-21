package com.wutsi.koki.portal.client.module.model

import com.wutsi.koki.common.dto.ObjectType

data class ModuleModel(
    val id: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val detailsUrl: String? = null,
    val objectType: ObjectType = ObjectType.UNKNOWN,
    val homeUrl: String? = null,
    val tabUrl: String? = null,
    val settingsUrl: String? = null,
    val jsUrl: String? = null,
    val cssUrl: String? = null,
)
