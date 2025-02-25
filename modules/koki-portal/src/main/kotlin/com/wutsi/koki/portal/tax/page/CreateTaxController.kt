package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.form.TaxForm
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.portal.tenant.service.TypeService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["tax:manage"])
class CreateTaxController(
    private val service: TaxService,
    private val accountService: AccountService,
    private val typeService: TypeService,
) : AbstractTaxController() {
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

        model.addAttribute(
            "taxTypes",
            typeService.types(
                active = true,
                objectType = ObjectType.TAX,
                limit = Integer.MAX_VALUE,
            )
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TAX_CREATE,
                title = "New Tax Report",
            )
        )

        loadFiscalYears(model)
        return "taxes/create"
    }

    @PostMapping("/taxes/add-new")
    fun addNew(@ModelAttribute form: TaxForm, model: Model): String {
        try {
            val taxId = service.create(form)
            return "redirect:/taxes?_toast=$taxId&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, null, model)
        }
    }
}
