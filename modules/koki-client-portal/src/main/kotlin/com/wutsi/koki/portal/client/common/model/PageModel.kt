package com.wutsi.koki.portal.client.common.model

data class PageModel(
    val language: String = "en",
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val assetUrl: String = "",
)
