package com.wutsi.koki.invoice.server.io.pdf

import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.OutputStream

@Service
class InvoicePDFGenerator {
    fun generate(invoice: InvoiceEntity, output: OutputStream) {
        val doc = loadDocument()
        val renderer = ITextRenderer()
        val context = renderer.sharedContext
        context.isPrint = true
        context.isInteractive = true
        renderer.setDocumentFromString(doc.html())
        renderer.layout()
        renderer.createPDF(output)
    }

    private fun loadDocument(): Document {
        val input = InvoicePDFGenerator::class.java.getResourceAsStream("/invoice/default/template.html")
        val html = IOUtils.toString(input, "utf-8")
        val doc = Jsoup.parse(html, "utf-8")
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        return doc
    }
}
