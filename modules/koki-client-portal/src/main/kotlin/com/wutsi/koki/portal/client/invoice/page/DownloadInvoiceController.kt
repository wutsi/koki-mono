package com.wutsi.koki.portal.client.invoice.page

import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.invoice.model.InvoiceModel
import com.wutsi.koki.portal.client.invoice.service.InvoiceService
import com.wutsi.koki.portal.client.security.RequiresModule
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URL

@RestController
@RequestMapping("/invoices")
@RequiresModule(name = "invoice")
class DownloadInvoiceController(
    private val service: InvoiceService,
    private val response: HttpServletResponse,
) : AbstractPageController() {
    @GetMapping("/{id}/download")
    fun download(@PathVariable id: Long) {
        val invoice = service.invoice(id)
        if (isOwner(invoice)) {
            val filename = "Invoice-${invoice.number}.pdf"
            response.contentType = "application/pdf"
            response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(filename).build().toString()
            )
            val url = URL(service.pdfUrl(id))
            url.openStream().use { input ->
                IOUtils.copy(input, response.outputStream)
            }
        } else {
            // Access error
            response.sendRedirect("/error/access-denied")
        }
    }

    private fun isOwner(invoice: InvoiceModel): Boolean {
        val user = userHolder.get()
        return invoice.customerAccountId == user?.accountId
    }
}
