package com.wutsi.koki.room.server.command

data class PublishRoomCommand(
    val roomId: Long = -1,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis()
)
