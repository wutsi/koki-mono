package com.wutsi.koki.portal.invoice.page

import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.invoice.service.InvoiceService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["invoice"])
class ListInvoiceController(
    private val service: InvoiceService
) : AbstractInvoiceController() {
    @GetMapping("/invoices")
    fun list(
        @RequestParam(required = false) status: InvoiceStatus? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.INVOICE_LIST,
                title = "Invoices",
            )
        )

        model.addAttribute("statuses", InvoiceStatus.entries.filter { entry -> entry != InvoiceStatus.UNKNOWN })
        model.addAttribute("status", status)

        more(status, limit, offset, model)
        return "invoices/list"
    }

    @GetMapping("/invoices/more")
    fun more(
        @RequestParam(required = false) status: InvoiceStatus? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val invoices = service.invoices(
            statuses = status?.let { listOf(status) } ?: emptyList(),
            limit = limit,
            offset = offset,
        )
        if (invoices.isNotEmpty()) {
            model.addAttribute("invoices", invoices)
            if (invoices.size >= limit) {
                val nextOffset = offset + limit
                var url = "/invoices/more?limit=$limit&offset=$nextOffset"
                if (status != null) {
                    url = "$url&status=$status"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "invoices/more"
    }
}
