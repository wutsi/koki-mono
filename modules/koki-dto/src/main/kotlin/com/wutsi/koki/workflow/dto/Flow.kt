package com.wutsi.koki.workflow.dto

data class Flow(
    val id: Long = -1,
    val fromId: Long = -1,
    val toId: Long = -1,
    val expression: String? = null
)
