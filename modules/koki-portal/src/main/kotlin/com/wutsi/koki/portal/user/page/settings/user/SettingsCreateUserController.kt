package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.model.UserForm
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/settings/users")
class SettingsCreateUserController(
    private val service: UserService
) : AbstractPageController() {
    @GetMapping("/create")
    fun create(model: Model): String {
        val form = UserForm()
        return create(form, model)
    }

    private fun create(form: UserForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_USER_CREATE,
                title = "Create User",
            )

        )
        return "users/settings/users/create"
    }

    @PostMapping("/add-new")
    fun addNew(
        @ModelAttribute form: UserForm,
        model: Model
    ): String {
        try {
            val userId = service.create(form)
            return "redirect:/settings/users?_toast=$userId&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)
            return create(form, model)
        }
    }
}
