package com.wutsi.koki.invoice.server.io.pdf

import com.wutsi.koki.invoice.server.io.html.InvoiceHtmlExporter
import org.springframework.stereotype.Service

@Service
class InvoicePdfExporter(
    htmlExporter: InvoiceHtmlExporter
) : AbstractPdfExporter(htmlExporter)
