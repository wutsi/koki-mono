package com.wutsi.koki.portal.tax.model

data class TaxTypeModel(
    val id: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val active: Boolean = false,
)
