package com.wutsi.koki.common.dto

data class ImportMessage(
    val location: String = "",
    val code: String = "",
    val message: String? = null,
)
