package com.wutsi.koki.portal.account.page

import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.client.HttpClientErrorException

@Controller
class AccountController(
    private val service: AccountService,
) : AbstractPageController() {
    @GetMapping("/accounts/{id}")
    fun show(
        @PathVariable id: Long,
        model: Model
    ): String {
        val account = service.account(id)
        return show(account, model)
    }

    private fun show(account: AccountModel, model: Model): String {
        model.addAttribute("account", account)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ACCOUNT,
                title = account.name,
            )
        )
        return "accounts/show"
    }

    @GetMapping("/accounts/{id}/delete")
    fun delete(@PathVariable id: Long, model: Model): String {
        val account = service.account(id)
        try {
            service.delete(id)

            model.addAttribute("account", account)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.ACCOUNT_DELETED,
                    title = account.name,
                )
            )
            return "accounts/deleted"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(account, model)
        }
    }
}
