package com.wutsi.koki.email.server.mq

interface EmailWorker {
    fun notify(event: Any): Boolean
}
