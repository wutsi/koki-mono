package com.wutsi.koki.event.server.rabbitmq

import java.util.UUID

data class RabbitMQEvent(
    val id: String = UUID.randomUUID().toString(),
    val classname: String = "",
    val payload: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
