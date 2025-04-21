package com.wutsi.koki.portal.client.invoice.page

import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/invoices")
class InvoiceListController : AbstractPageController() {
    @GetMapping
    fun list(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.INVOICE_LIST,
                title = "Invoices"
            )
        )
        return "invoices/list"
    }
}
