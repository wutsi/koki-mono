package com.wutsi.koki.portal.pub.common.model

import java.util.UUID

data class PageModel(
    val language: String = "en",
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val type: String = "website",
    val image: String? = null,
    val assetUrl: String = "",
    val url: String? = null,
    val hitId: String = UUID.randomUUID().toString(),
)
