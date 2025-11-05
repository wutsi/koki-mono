package com.wutsi.koki.agent.dto

import java.util.Date

data class AgentSummary(
    val id: Long = -1,
    val userId: Long = -1,
    val totalSales: Long? = null,
    val totalRentals: Long? = null,
    val past12mSales: Long? = null,
    val past12mRentals: Long? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
