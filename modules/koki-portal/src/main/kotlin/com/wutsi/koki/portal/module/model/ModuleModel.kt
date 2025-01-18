package com.wutsi.koki.portal.module.model

data class ModuleModel(
    val id: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val homeUrl: String? = null,
    val tabUrl: String? = null,
    val settingsUrl: String? = null,
    val permissions: List<PermissionModel> = emptyList(),
)
