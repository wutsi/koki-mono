package com.wutsi.koki.platform.messaging

interface MessagingService {
    @Throws(MessagingException::class)
    fun send(message: Message): String
}
