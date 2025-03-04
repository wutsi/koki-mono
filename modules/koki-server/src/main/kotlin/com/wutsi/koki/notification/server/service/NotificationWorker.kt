package com.wutsi.koki.notification.server.mq

interface NotificationWorker {
    fun notify(event: Any): Boolean
}
