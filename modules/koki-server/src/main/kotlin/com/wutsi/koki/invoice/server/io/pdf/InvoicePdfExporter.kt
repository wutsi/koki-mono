package com.wutsi.koki.invoice.server.io.pdf

import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.io.html.InvoiceHtmlExporter
import org.springframework.stereotype.Service
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.ByteArrayOutputStream
import java.io.OutputStream

@Service
class InvoicePdfExporter(
    private val htmlExporter: InvoiceHtmlExporter
) {
    fun export(invoice: InvoiceEntity, output: OutputStream) {
        val renderer = ITextRenderer()
        val context = renderer.sharedContext
        context.isPrint = true
        context.isInteractive = true
        renderer.setDocumentFromString(html(invoice))
        renderer.layout()
        renderer.createPDF(output)
    }

    private fun html(invoice: InvoiceEntity): String {
        val output = ByteArrayOutputStream()
        htmlExporter.export(invoice, output)
        return output.toString("utf-8")
    }
}
