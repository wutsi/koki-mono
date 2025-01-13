package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.tax.form.TaxStatusForm
import com.wutsi.koki.portal.tax.model.TaxModel
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.tax.dto.TaxStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class ChangeTaxStatusController(
    private val service: TaxService,
) : AbstractPageController() {
    @GetMapping("/taxes/{id}/status")
    fun edit(
        @PathVariable id: Long,
        model: Model
    ): String {
        val tax = service.tax(id)
        val form = TaxStatusForm(
            assigneeId = tax.assignee?.id,
            status = tax.status,
        )
        return edit(tax, form, model)
    }

    private fun edit(tax: TaxModel, form: TaxStatusForm, model: Model): String {
        model.addAttribute("tax", tax)
        model.addAttribute("form", form)
        model.addAttribute("statuses", TaxStatus.values())
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.TAX_STATUS,
                title = tax.name
            )
        )
        return "taxes/status"
    }

    @PostMapping("/taxes/{id}/status")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: TaxStatusForm,
        model: Model
    ): String {
        try {
            service.status(id, form)
            return "redirect:/taxes/$id"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            val tax = service.tax(id)
            return edit(tax, form, model)
        }
    }
}
