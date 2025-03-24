package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.form.TaxAssigneeForm
import com.wutsi.koki.portal.tax.form.TaxStatusForm
import com.wutsi.koki.portal.tax.model.TaxModel
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.tax.dto.TaxStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["tax:manage"])
class ChangeTaxAssigneeController(
    private val service: TaxService
) : AbstractTaxController() {
    @GetMapping("/taxes/{id}/assignee")
    fun edit(
        @PathVariable id: Long,
        model: Model
    ): String {
        val tax = service.tax(id)
        val form = TaxAssigneeForm(
            assigneeId = tax.assignee?.id,
        )
        return edit(tax, form, model)
    }

    private fun edit(tax: TaxModel, form: TaxAssigneeForm, model: Model): String {
        model.addAttribute("tax", tax)
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TAX_ASSIGNEE,
                title = tax.name
            )
        )

        return "taxes/assignee"
    }

    @PostMapping("/taxes/{id}/assignee")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: TaxAssigneeForm,
        model: Model
    ): String {
        try {
            service.assignee(id, form)
            return "redirect:/taxes/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            val tax = service.tax(id)
            return edit(tax, form, model)
        }
    }
}
