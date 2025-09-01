package com.wutsi.koki.portal.common.model

data class ResultSetModel<T>(
    val total: Long = 0L,
    val items: List<T> = emptyList<T>()
)
