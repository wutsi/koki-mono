package com.wutsi.koki.platform.messaging

interface MessagingService {
    fun send(message: Message): String
}
