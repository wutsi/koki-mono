package com.wutsi.koki.portal.payment.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.payment.service.TransactionService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["payment"])
class PaymentTabController(
    private val service: TransactionService
) : AbstractPaymentController() {
    @GetMapping("/payments/tab")
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("testMode", testMode)

        more(ownerId, ownerType, limit, offset, model)
        return "payments/tab"
    }

    @GetMapping("/payments/tab/more")
    fun more(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("showInvoice", false)

        val transactions = if (ownerType == ObjectType.INVOICE) {
            service.transactions(
                invoiceId = ownerId,
                limit = limit,
                offset = offset,
            )
        } else {
            emptyList()
        }
        if (transactions.isNotEmpty()) {
            model.addAttribute("payments", transactions)
            if (transactions.size >= limit) {
                val nextOffset = offset + limit
                var url = "/payments/tab/more?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-type=$ownerType"
                model.addAttribute("moreUrl", url)
            }
        }
        return "payments/more"
    }
}
