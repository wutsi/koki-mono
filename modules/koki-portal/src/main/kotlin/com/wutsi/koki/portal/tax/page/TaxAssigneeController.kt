package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.form.TaxAssigneeForm
import com.wutsi.koki.portal.tax.model.TaxModel
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["tax:manage"])
class TaxAssigneeController(
    private val service: TaxService,
    private val userService: UserService,
) : AbstractTaxController() {
    @GetMapping("/taxes/{id}/assignee")
    fun edit(
        @PathVariable id: Long,
        model: Model
    ): String {
        val tax = service.tax(id)
        val form = TaxAssigneeForm(assigneeId = -1)
        return edit(tax, form, model)
    }

    private fun edit(tax: TaxModel, form: TaxAssigneeForm, model: Model): String {
        model.addAttribute("tax", tax)
        model.addAttribute("form", form)

        if (form.assigneeId != null && form.assigneeId != -1L) {
            val assignee = userService.user(form.assigneeId, fullGraph = false)
            model.addAttribute("assignee", assignee)
        }

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
