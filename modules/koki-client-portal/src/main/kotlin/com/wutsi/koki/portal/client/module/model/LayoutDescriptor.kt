package com.wutsi.koki.portal.client.module.model

data class PageLayoutDescriptor(
    val tabs: Map<String, List<String>> = emptyMap()
)
