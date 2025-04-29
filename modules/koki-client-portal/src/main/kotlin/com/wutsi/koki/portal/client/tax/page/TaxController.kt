package com.wutsi.koki.portal.client.tax.page

import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.common.page.PageName
import com.wutsi.koki.portal.client.security.RequiresModule
import com.wutsi.koki.portal.client.tax.model.TaxModel
import com.wutsi.koki.portal.client.tax.service.TaxService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/taxes")
@RequiresModule("tax")
class TaxController(
    private val service: TaxService,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long, model: Model): String {
        val tax = service.tax(id)
        if (isOwner(tax)) {
            model.addAttribute("tax", tax)

            model.addAttribute(
                "page",
                createPageModel(
                    name = PageName.TAX,
                    title = tax.name,
                )
            )

            return "taxes/show"
        } else {
            return "redirect:/error/access-denied"
        }
    }

    private fun isOwner(tax: TaxModel): Boolean {
        val user = userHolder.get()
        return tax.account.id == user?.accountId
    }
}
