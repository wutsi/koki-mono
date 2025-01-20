package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.tax.model.TaxModel
import com.wutsi.koki.portal.tax.service.TaxService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.client.HttpClientErrorException

@Controller
class TaxController(
    private val service: TaxService,
) : AbstractTaxDetailsController() {
    @GetMapping("/taxes/{id}")
    fun show(
        @PathVariable id: Long,
        model: Model
    ): String {
        val tax = service.tax(id)
        model.addAttribute("tax", tax)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TAX,
                title = tax.name,
            )
        )
        return "taxes/show"
    }

    fun show(tax: TaxModel, model: Model): String {
        model.addAttribute("tax", tax)
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
    fun delete(@PathVariable id: Long, model: Model): String {
        val tax = service.tax(id)
        try {
            service.delete(id)
            model.addAttribute("tax", tax)
            model.addAttribute(
                "page",
                createPageModel(
                    name = PageName.TAX_DELETED,
                    title = tax.name,
                )
            )
            return "taxes/deleted"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(tax, model)
        }
    }
}
