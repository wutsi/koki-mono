package com.wutsi.koki.portal.client.invoice.page

import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.common.page.PageName
import com.wutsi.koki.portal.client.invoice.service.InvoiceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/invoices")
class ListInvoiceController(
    private val service: InvoiceService,
) : AbstractPageController() {
    @GetMapping
    fun list(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.INVOICE_LIST,
                title = "Invoices"
            )
        )
        more(limit = 20, offset = 0, model)
        return "invoices/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val invoices = service.invoices(
            limit = limit,
            offset = offset,
        )
        if (invoices.isNotEmpty()) {
            model.addAttribute("invoices", invoices)
            if (invoices.size >= limit) {
                val nextOffset = offset + limit
                var url = "/invoices/more?limit=$limit&offset=$nextOffset"
                model.addAttribute("moreUrl", url)
            }
        }

        return "invoices/more"
    }
}
