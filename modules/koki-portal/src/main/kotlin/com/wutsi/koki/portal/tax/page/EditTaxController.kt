package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.form.TaxForm
import com.wutsi.koki.portal.tax.model.TaxModel
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.portal.tenant.service.TypeService
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException
import java.text.SimpleDateFormat

@Controller
@RequiresPermission(["tax:manage"])
class EditTaxController(
    private val service: TaxService,
    private val accountService: AccountService,
    private val typeService: TypeService,
    private val userService: UserService,
) : AbstractTaxController() {
    @GetMapping("/taxes/{id}/edit")
    fun edit(
        @PathVariable id: Long,
        model: Model
    ): String {
        val tax = service.tax(id)
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val form = TaxForm(
            accountId = tax.account.id,
            accountantId = tax.accountant?.id,
            assigneeId = tax.assignee?.id,
            technicianId = tax.technician?.id,
            fiscalYear = tax.fiscalYear,
            taxTypeId = tax.taxType?.id,
            description = tax.description,
            startAt = tax.startAt?.let { date -> fmt.format(date) } ?: "",
            dueAt = tax.dueAt?.let { date -> fmt.format(date) } ?: "",
        )
        return edit(tax, form, model)
    }

    private fun edit(tax: TaxModel, form: TaxForm, model: Model): String {
        model.addAttribute("tax", tax)
        model.addAttribute("form", form)

        model.addAttribute(
            "taxTypes",
            typeService.types(
                objectType = ObjectType.TAX,
                limit = Integer.MAX_VALUE,
            ).filter { type ->
                type.id == tax.taxType?.id || type.active
            }
        )

        model.addAttribute("account", accountService.account(form.accountId))

        val userIds = listOf(form.assigneeId, form.accountantId, form.technicianId)
            .filterNotNull()
            .toSet()
        val users = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.users(ids = userIds.toList(), limit = userIds.size)
                .associateBy { user -> user.id }
        }
        form.accountantId?.let { id -> model.addAttribute("accountant", users[id]) }
        form.technicianId?.let { id -> model.addAttribute("technician", users[id]) }
        form.assigneeId?.let { id -> model.addAttribute("assignee", users[id]) }

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TAX_EDIT,
                title = "New Tax Report",
            )
        )

        loadFiscalYears(model)
        return "taxes/edit"
    }

    @PostMapping("/taxes/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: TaxForm,
        model: Model
    ): String {
        try {
            service.update(id, form)
            return "redirect:/taxes/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            val tax = service.tax(id)
            return edit(tax, form, model)
        }
    }
}
