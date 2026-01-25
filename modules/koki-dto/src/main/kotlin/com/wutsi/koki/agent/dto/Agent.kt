package com.wutsi.koki.agent.dto

import java.util.Date

data class Agent(
    val id: Long = -1,
    val userId: Long = -1,
    val qrCodeUrl: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
