package com.wutsi.koki.tracking.server.domain

data class KpiRoomEntity(
    val tenantId: Long? = null,
    val productId: String? = null,
    val totalImpressions: Long = 0L,
    val totalViews: Long = 0L,
    val totalClicks: Long = 0L,
    val totalMessages: Long = 0L,
    val totalVisitors: Long = 0L,
)
