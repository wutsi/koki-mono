package com.wutsi.koki.portal.account.page

import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
class AccountController(
    private val service: AccountService,
) : AbstractAccountDetailsController() {
    @GetMapping("/accounts/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        val account = service.account(id)
        if (toast == id && canShowToasts(timestamp, referer, listOf("/accounts/$id/edit", "/accounts/create"))) {
            model.addAttribute("toast", "Saved")
        }
        return show(account, model)
    }

    private fun show(account: AccountModel, model: Model): String {
        model.addAttribute("account", account)
        model.addAttribute(
            "page",
            createPageModel(
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
            return "redirect:/accounts?_op=del&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(account, model)
        }
    }
}
