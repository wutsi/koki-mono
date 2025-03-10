package com.wutsi.koki.portal.payment.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.service.TransactionService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
@RequiresPermission(["payment"])
class PaymentController(
    private val service: TransactionService
) : AbstractDetailsPaymentController() {
    @GetMapping("/payments/{id}")
    fun show(
        @PathVariable id: String,
        model: Model
    ): String {
        val tx = service.transaction(id)
        model.addAttribute("payment", tx)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.PAYMENT,
                title = "Payments #${tx.id}",
            )
        )
        return "payments/show"
    }
}
