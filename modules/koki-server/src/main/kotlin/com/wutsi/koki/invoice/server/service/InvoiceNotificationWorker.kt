package com.wutsi.koki.invoice.server.service

import com.wutsi.koki.platform.mq.Consumer
import org.springframework.stereotype.Service

@Service
class InvoiceConsumer: Consumer {
    override fun consume(event: Any): Boolean{
        return false
    }
}
