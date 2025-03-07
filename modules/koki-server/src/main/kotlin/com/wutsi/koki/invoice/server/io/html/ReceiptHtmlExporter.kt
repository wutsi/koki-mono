package com.wutsi.koki.invoice.server.io.html

import org.springframework.stereotype.Service

@Service
class ReceiptHtmlExporter : AbstractHtmlExporter() {
    override fun getHtml(): String {
        return getHtml("/invoice/template/default/receipt.html")
    }
}
