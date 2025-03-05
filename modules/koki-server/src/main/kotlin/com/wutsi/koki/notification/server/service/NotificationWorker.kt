package com.wutsi.koki.notification.server.service

interface NotificationWorker {
    fun notify(event: Any): Boolean
}
