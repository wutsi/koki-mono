package com.wutsi.koki.portal.error.page

import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping
class PaymentNotSupportedController : AbstractPageController() {
    @GetMapping("/error/payment-not-supported")
    fun error(
        @RequestParam(required = false, name = "payment-method-type") paymentMethodType: PaymentMethodType? = null,
        model: Model,
    ): String {
        model.addAttribute("type", paymentMethodType)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ERROR_PAYMENT_NOT_SUPPORTED,
                title = "Payment Not Supported"
            )
        )
        return "/error/payment-not-supported"
    }
}
