package com.wutsi.koki.event.server.rabbitmq

data class TestEvent(
    val name: String = "",
    val value: Int = 0,
)
