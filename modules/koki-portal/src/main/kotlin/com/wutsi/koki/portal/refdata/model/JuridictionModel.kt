package com.wutsi.koki.portal.refdata.model

data class JuridictionModel(
    val id: Long = -1,
    val state: LocationModel? = null,
    val country: String = "",
    val name: String = "",
)
