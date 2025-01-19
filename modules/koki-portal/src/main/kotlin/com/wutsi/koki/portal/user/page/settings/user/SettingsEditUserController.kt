package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.model.UserForm
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/settings/users")
class SettingsEditUserController(
    private val service: UserService
) : AbstractPageController() {
    @GetMapping("/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val user = service.user(id)
        val form = UserForm(
            displayName = user.displayName,
            email = user.email,
            status = user.status,
        )
        return edit(user, form, model)
    }

    private fun edit(user: UserModel, form: UserForm, model: Model): String {
        model.addAttribute("user", user)
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_USER_EDIT,
                title = user.displayName,
            )

        )
        return "users/settings/users/edit"
    }

    @PostMapping("/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: UserForm,
        model: Model
    ): String {
        try {
            service.update(id, form)
            return "redirect:/settings/users/$id?updated=$id"
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)
            return edit(UserModel(id = id), form, model)
        }
    }
}
