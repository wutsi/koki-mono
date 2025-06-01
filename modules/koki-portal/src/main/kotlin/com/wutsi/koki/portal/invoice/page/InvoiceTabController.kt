package com.wutsi.koki.portal.invoice.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.invoice.model.InvoiceModel
import com.wutsi.koki.portal.invoice.service.InvoiceService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["invoice"])
class InvoiceTabController(
    private val service: InvoiceService,
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
        return "invoices/tab"
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

    fun loadMore(
        ownerId: Long, ownerType: ObjectType, limit: Int, offset: Int, model: Model
    ): List<InvoiceModel> {
        val invoices = if (ownerType == ObjectType.ACCOUNT) {
            service.invoices(
                accountId = ownerId,
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
                    "/invoices/tab/more?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-type=$ownerType"
                model.addAttribute("moreUrl", url)
            }
        }

        return invoices
    }
}
