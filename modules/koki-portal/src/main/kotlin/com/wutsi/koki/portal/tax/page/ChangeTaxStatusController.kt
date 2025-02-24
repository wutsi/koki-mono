package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
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
@RequiresPermission(["tax:status"])
class ChangeTaxStatusController(
    private val service: TaxService,
    private val userService: UserService,
) : AbstractTaxController() {
    @GetMapping("/taxes/{id}/status")
    fun edit(
        @PathVariable id: Long,
        model: Model
    ): String {
        val tax = service.tax(id)
        val form = TaxStatusForm(
            assigneeId = null,
            status = tax.status,
        )
        return edit(tax, form, model)
    }

    private fun edit(tax: TaxModel, form: TaxStatusForm, model: Model): String {
        model.addAttribute("tax", tax)
        model.addAttribute("form", form)
        model.addAttribute("statuses", TaxStatus.entries)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TAX_STATUS,
                title = tax.name
            )
        )

        if (form.assigneeId != null) {
            val assignee = userService.user(form.assigneeId)
            model.addAttribute("assignee", assignee)
        }
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
            return "redirect:/taxes/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            val tax = service.tax(id)
            return edit(tax, form, model)
        }
    }
}
