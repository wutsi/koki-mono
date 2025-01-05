package com.wutsi.koki.portal.account.page

import com.wutsi.koki.portal.account.form.AccountForm
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.account.service.AttributeService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException
import java.util.Locale

@Controller
class EditAccountController(
    private val service: AccountService,
    private val attributeService: AttributeService,
    private val userService: UserService,
    private val request: HttpServletRequest,
) : AbstractPageController() {
    @GetMapping("/accounts/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val account = service.account(id)
        val form = AccountForm(
            managedById = account.managedBy?.id ?: -1,
            language = account.language,
            phone = account.phone,
            description = account.description,
            website = account.website,
            email = account.email,
            mobile = account.mobile,
            name = account.name,
            attributes = account.attributes.map { attr -> attr.attribute.id to attr.value }.toMap()
        )
        return edit(model, form, account)
    }

    private fun edit(model: Model, form: AccountForm, account: AccountModel): String {
        model.addAttribute("form", form)
        model.addAttribute("account", account)

        val attributes = attributeService.attributes(
            active = true,
            limit = Integer.MAX_VALUE,
        )
        if (attributes.isNotEmpty()) {
            model.addAttribute("attributes", attributes)
        }

        val languages = Locale.getISOLanguages()
            .map { lang -> Locale(lang) }
            .toSet()
            .sortedBy { it.getDisplayLanguage() }
        model.addAttribute("languages", languages)

        val users = userService.users()
        model.addAttribute("users", users)

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ACCOUNT_EDIT,
                title = form.name
            )
        )
        return "accounts/edit"
    }

    @PostMapping("/accounts/{id}/update")
    fun update(@PathVariable id: Long, @ModelAttribute form: AccountForm, model: Model): String {
        val account = service.account(id)
        try {
            val attributes = attributeService.attributes(
                active = true,
                limit = Integer.MAX_VALUE,
            )

            service.update(
                id,
                form.copy(
                    attributes = attributes
                        .map { attr -> attr.id to request.getParameter("attribute-${attr.id}") }
                        .toMap()
                )
            )

            model.addAttribute("account", account)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.ACCOUNT_SAVED,
                    title = form.name
                )
            )
            return "accounts/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(model, form, account)
        }
    }
}
