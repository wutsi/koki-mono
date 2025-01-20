package com.wutsi.koki.portal.module.model

data class PageLayoutDescriptor(
    val tabs: Map<String, List<String>> = emptyMap()
)
