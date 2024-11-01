package com.wutsi.koki.error.dto

data class Parameter(
    val name: String = "",
    val type: ParameterType? = null,
    val value: Any? = null,
)
