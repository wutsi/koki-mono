package com.wutsi.koki.event.server.rabbitmq

interface RabbitMQHandler {
    fun handle(payload: Any): Boolean
}
