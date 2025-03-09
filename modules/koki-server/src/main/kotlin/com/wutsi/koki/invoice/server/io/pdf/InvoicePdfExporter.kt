package com.wutsi.koki.invoice.server.io.pdf

import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.io.html.InvoiceHtmlExporter
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.ByteArrayOutputStream
import java.io.OutputStream

open class PdfExporter(
    private val htmlExporter: InvoiceHtmlExporter
) {
    fun export(invoice: InvoiceEntity, business: BusinessEntity, output: OutputStream) {
        val renderer = ITextRenderer()
        val context = renderer.sharedContext
        context.isPrint = true
        context.isInteractive = true
        renderer.setDocumentFromString(html(invoice, business))
        renderer.layout()
        renderer.createPDF(output)
    }

    private fun html(invoice: InvoiceEntity, business: BusinessEntity): String {
        val output = ByteArrayOutputStream()
        htmlExporter.export(invoice, business, output)
        return output.toString("utf-8")
    }
}
