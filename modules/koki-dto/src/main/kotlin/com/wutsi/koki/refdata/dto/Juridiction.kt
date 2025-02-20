package com.wutsi.koki.refdata.dto

data class Juridiction(
    val id: Long = -1,
    val country: String = "",
    val stateId: Long? = null,
)
