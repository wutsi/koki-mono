package com.wutsi.koki.portal.module.model

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
    val permissions: List<PermissionModel> = emptyList(),
) {
    fun toTabUrl(id: Long, module: ModuleModel): String? {
        return toTabUrl(id, module, false)
    }

    fun toTabUrl(id: Long, module: ModuleModel, readOnly: Boolean): String? {
        return tabUrl?.let { url ->
            "$url?owner-id=$id&owner-type=${module.objectType}" +
                if (readOnly) "&read-only=$readOnly" else ""
        }
    }
}
