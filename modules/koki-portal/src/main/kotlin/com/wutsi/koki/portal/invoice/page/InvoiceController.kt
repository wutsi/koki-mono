package com.wutsi.koki.portal.invoice.page

import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.invoice.service.InvoiceService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/invoices")
@RequiresPermission(["invoice"])
class InvoiceController(
    private val service: InvoiceService
) : AbstractDetailsInvoiceController() {
    @GetMapping("/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
        model: Model
    ): String {
        val invoice = service.invoice(id)
        model.addAttribute("invoice", invoice)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.INVOICE,
                title = "Invoice #${invoice.number}",
            )
        )

        loadToast(id, referer, toast, timestamp, operation, model)
        return "invoices/show"
    }

    private fun loadToast(
        id: Long,
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        operation: String?,
        model: Model
    ) {
        if (!canShowToasts(timestamp, referer, listOf("/invoices/$id", "/taxes"))) {
            return
        }

        if (operation == "approve" && id == toast) {
            model.addAttribute("toast", "The invoice is approved")
        } else if (operation == "void" && id == toast) {
            model.addAttribute("toast", "The invoice is voided")
        } else if (operation == "create" && id == toast) {
            model.addAttribute("toast", "invoice created")
        }
    }

    @GetMapping("/{id}/approve")
    @RequiresPermission(["invoice:manage"])
    fun approve(@PathVariable id: Long, model: Model): String {
        return setStatus(id, InvoiceStatus.OPENED, "approve", model)
    }

    @GetMapping("/{id}/void")
    @RequiresPermission(["invoice:manage"])
    fun void(@PathVariable id: Long, model: Model): String {
        return setStatus(id, InvoiceStatus.VOIDED, "void", model)
    }

    private fun setStatus(id: Long, status: InvoiceStatus, operation: String, model: Model): String {
        try {
            service.setStatus(id, status)
            return "redirect:/invoices/$id?_op=$operation&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(id = id, model = model)
        }
    }
}
