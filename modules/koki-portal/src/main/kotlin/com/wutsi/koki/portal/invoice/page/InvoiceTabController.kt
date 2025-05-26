package com.wutsi.koki.portal.invoice.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.portal.invoice.model.InvoiceModel
import com.wutsi.koki.portal.invoice.service.InvoiceService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.service.TaxProductService
import com.wutsi.koki.portal.tax.service.TaxService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["invoice"])
class InvoiceTabController(
    private val service: InvoiceService,
    private val taxService: TaxService,
    private val taxProductService: TaxProductService,
) : AbstractInvoiceController() {
    @GetMapping("/invoices/tab")
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("testMode", testMode)
        val invoices = loadMore(ownerId, ownerType, limit, offset, model)
        loadCreateUrl(ownerId, ownerType, invoices, model)
        return "invoices/tab"
    }

    private fun loadCreateUrl(ownerId: Long, ownerType: ObjectType, invoices: List<InvoiceModel>, model: Model) {
        // owner should be tax
        if (ownerType != ObjectType.TAX) {
            return
        }

        // There should not be already an invoice
        val invoice = invoices.find { inv ->
            inv.status != InvoiceStatus.VOIDED && inv.status != InvoiceStatus.UNKNOWN
        }
        if (invoice != null) {
            return
        }

        // Tax should have product
        val tax = taxService.tax(id = ownerId, fullGraph = false)
        if (tax.productCount <= 0) {
            return
        }

        model.addAttribute("createUrl", "/invoices/tab/create-invoice?tax-id=${tax.id}")
    }

    @GetMapping("/invoices/tab/more")
    fun more(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        loadMore(ownerId, ownerType, limit, offset, model)
        return "invoices/more"
    }

    @GetMapping("/invoices/tab/create-invoice")
    @RequiresPermission(["tax:manage", "invoice:manage"])
    fun createInvoice(@RequestParam(name = "tax-id") taxId: Long): String {
        val tax = taxService.tax(taxId)
        val taxProducts = taxProductService.products(
            taxIds = listOf(taxId),
            limit = Integer.MAX_VALUE,
        )
        val invoiceId = service.createInvoice(tax, taxProducts)
        return "redirect:/invoices/$invoiceId?_op=create&_toast=$invoiceId&_ts=" + System.currentTimeMillis()
    }

    fun loadMore(
        ownerId: Long, ownerType: ObjectType, limit: Int, offset: Int, model: Model
    ): List<InvoiceModel> {
        val invoices = if (ownerType == ObjectType.ACCOUNT) {
            service.invoices(
                accountId = ownerId,
                limit = limit,
                offset = offset,
            )
        } else if (ownerType == ObjectType.TAX) {
            service.invoices(
                taxId = ownerId,
                limit = limit,
                offset = offset,
            )
        } else {
            emptyList()
        }
        if (invoices.isNotEmpty()) {
            model.addAttribute("invoices", invoices)
            if (invoices.size >= limit) {
                val nextOffset = offset + limit
                var url =
                    "/invoices/tab/more.html?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-type=$ownerType"
                model.addAttribute("moreUrl", url)
            }
        }

        return invoices
    }
}
