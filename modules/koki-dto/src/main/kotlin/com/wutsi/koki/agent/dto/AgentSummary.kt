package com.wutsi.koki.agent.dto

import java.util.Date

data class AgentSummary(
    val id: Long = -1,
    val userId: Long = -1,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
