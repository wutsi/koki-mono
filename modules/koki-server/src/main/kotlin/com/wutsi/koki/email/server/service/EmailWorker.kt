package com.wutsi.koki.email.server.service

interface EmailWorker {
    fun notify(event: Any): Boolean
}
