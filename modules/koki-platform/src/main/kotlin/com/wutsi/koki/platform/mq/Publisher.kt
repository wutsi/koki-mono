package com.wutsi.koki.platform.mq

interface Publisher {
    fun publish(event: Any)
}
