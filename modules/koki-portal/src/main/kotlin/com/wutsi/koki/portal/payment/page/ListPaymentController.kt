package com.wutsi.koki.portal.payment.page

import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.service.TransactionService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["payment"])
class ListPaymentController(
    private val service: TransactionService
) : AbstractPaymentController() {
    @GetMapping("/payments")
    fun list(
        @RequestParam(required = false) status: TransactionStatus? = null,
        @RequestParam(required = false) type: TransactionType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.PAYMENT_LIST,
                title = "Payments",
            )
        )

        model.addAttribute("statuses", TransactionStatus.entries.filter { entry -> entry != TransactionStatus.UNKNOWN })
        model.addAttribute("status", status)

        model.addAttribute("types", TransactionType.entries.filter { entry -> entry != TransactionType.UNKNOWN })
        model.addAttribute("type", type)

        more(status, type, limit, offset, model)
        return "payments/list"
    }

    @GetMapping("/payments/more")
    fun more(
        @RequestParam(required = false) status: TransactionStatus? = null,
        @RequestParam(required = false) type: TransactionType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("showInvoice", true)

        val transactions = service.transactions(
            statuses = status?.let { listOf(status) } ?: emptyList(),
            types = type?.let { listOf(type) } ?: emptyList(),
            limit = limit,
            offset = offset,
        )
        if (transactions.isNotEmpty()) {
            model.addAttribute("payments", transactions)
            if (transactions.size >= limit) {
                val nextOffset = offset + limit
                var url = "/payments/more?limit=$limit&offset=$nextOffset"
                if (status != null) {
                    url = "$url&status=$status"
                }
                if (type != null) {
                    url = "$url&type=$type"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "payments/more"
    }
}
