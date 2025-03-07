package com.wutsi.koki.invoice.server.io.html

import org.springframework.stereotype.Service

@Service
class InvoiceHtmlExporter : AbstractHtmlExporter() {
    override fun getHtml(): String {
        return getHtml("/invoice/template/default/invoice.html")
    }
}
