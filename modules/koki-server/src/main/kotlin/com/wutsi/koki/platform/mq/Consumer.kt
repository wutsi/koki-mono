package com.wutsi.koki.platform.mq

interface Consumer {
    fun consume(event: Any)
}
