package com.wutsi.koki.email.server.mq

interface Mailet {
    fun service(event: Any): Boolean
}
