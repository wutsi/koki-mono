package com.wutsi.koki.portal.common.model

import com.wutsi.koki.portal.module.model.ModuleModel

data class PageModel(
    val language: String = "en",
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val tabs: Map<String, List<ModuleModel>> = emptyMap(),
    val assetUrl: String = "",
)
