package com.wutsi.koki.invoice.server.io.pdf

import com.wutsi.koki.invoice.server.io.html.ReceiptHtmlExporter
import org.springframework.stereotype.Service

@Service
class ReceiptPdfExporter(
    htmlExporter: ReceiptHtmlExporter
) : AbstractPdfExporter(htmlExporter)
