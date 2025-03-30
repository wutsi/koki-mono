package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.invoice.service.InvoiceService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.model.TaxModel
import com.wutsi.koki.portal.tax.service.TaxProductService
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.tax.dto.TaxStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["tax"])
class TaxController(
    private val service: TaxService,
    private val invoiceService: InvoiceService,
    private val taxProductService: TaxProductService,
) : AbstractTaxDetailsController() {
    @GetMapping("/taxes/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        val tax = service.tax(id)
        if (
            toast == id &&
            canShowToasts(timestamp, referer, listOf("/taxes/$id/edit", "/taxes/$id/status", "/taxes/create"))
        ) {
            model.addAttribute("toast", "Saved")
        }
        return show(tax, model)
    }

    fun show(tax: TaxModel, model: Model): String {
        model.addAttribute("tax", tax)
        model.addAttribute("statuses", TaxStatus.entries.filter { status -> status != TaxStatus.UNKNOWN })

        val invoices = invoiceService.invoices(
            taxId = tax.id,
            statuses = InvoiceStatus.entries.filter { status ->
                status != InvoiceStatus.UNKNOWN && status != InvoiceStatus.VOIDED
            },
            limit = 1,
        )
        model.addAttribute("invoice", invoices.firstOrNull())

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TAX,
                title = tax.name,
            )
        )
        return "taxes/show"
    }

    @GetMapping("/taxes/{id}/delete")
    @RequiresPermission(["tax:delete"])
    fun delete(@PathVariable id: Long, model: Model): String {
        val tax = service.tax(id)
        try {
            service.delete(id)
            return "redirect:/taxes?_op=del&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(tax, model)
        }
    }

    @GetMapping("/taxes/{id}/create-invoice")
    @RequiresPermission(["invoice:manage"])
    fun createInvoice(@PathVariable id: Long, model: Model): String {
        val tax = service.tax(id)
        val taxProducts = taxProductService.products(
            taxIds = listOf(id),
            limit = Integer.MAX_VALUE
        )
        if (taxProducts.isEmpty()) {
            model.addAttribute("error", ErrorCode.INVOICE_NO_PRODUCT)
            return show(tax, model)
        }

        try {
            val id = invoiceService.createInvoice(tax, taxProducts)
            return "redirect:/invoices/$id?op=create&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(tax, model)
        }
    }
}
