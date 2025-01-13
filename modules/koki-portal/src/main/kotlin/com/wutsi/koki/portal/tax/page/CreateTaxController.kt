package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.tax.form.TaxForm
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.portal.tax.service.TaxTypeService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.time.LocalDate

@Controller
class CreateTaxController(
    private val service: TaxService,
    private val accountService: AccountService,
    private val taxTypeService: TaxTypeService,
) : AbstractPageController() {
    @GetMapping("/taxes/create")
    fun create(
        @RequestParam(required = false, name = "account-id") accountId: Long? = null, model: Model
    ): String {
        val account = accountId?.let { id -> accountService.account(id) }
        val form = TaxForm(
            accountId = accountId ?: -1, accountantId = account?.managedBy?.id
        )
        return create(form, account, model)
    }

    private fun create(form: TaxForm, account: AccountModel?, model: Model): String {
        if (form.accountId > 0) {
            if (account == null) {
                model.addAttribute("account", accountService.account(form.accountId))
            } else {
                model.addAttribute("account", account)
            }
        }
        model.addAttribute("form", form)

        model.addAttribute("taxTypes", taxTypeService.taxTypes(active = true, limit = Integer.MAX_VALUE))

        model.addAttribute(
            "page", PageModel(
                name = PageName.TAX_CREATE,
                title = "New Tax Report",
            )
        )

        val year2 = LocalDate.now().year
        val year1 = year2 - 100
        val years = (year2 downTo year1).toList()
        model.addAttribute("years", years)
        return "taxes/create"
    }

    @PostMapping("/taxes/add-new")
    fun addNew(@ModelAttribute form: TaxForm, model: Model): String {
        try {
            val taxId = service.create(form)
            val tax = service.tax(taxId)
            model.addAttribute("tax", tax)
            model.addAttribute("createUrl", "/taxes/create")
            model.addAttribute(
                "page", PageModel(
                    name = PageName.TAX_SAVED, title = tax.name
                )
            )
            return "taxes/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, null, model)
        }
    }
}
