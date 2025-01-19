package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings/users")
class SettingsUserController(
    private val service: UserService,
    private val httpRequest: HttpServletRequest,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        @RequestParam(required = false) updated: Long? = null,
        model: Model
    ): String {
        val user = service.user(id)
        loadUpdatedToast(updated, model)
        return show(user, model)
    }

    private fun show(user: UserModel, model: Model): String {
        model.addAttribute("user", user)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_USER,
                title = user.displayName,
            )

        )
        return "users/settings/users/show"
    }

    private fun loadUpdatedToast(updated: Long?, model: Model) {
        val referer = httpRequest.getHeader(HttpHeaders.REFERER)
        if (
            updated != null &&
            referer != null &&
            (
                referer.endsWith("/settings/users/$updated/edit") ||
                    referer.endsWith("/settings/users/$updated/roles")
                )
        ) {
            model.addAttribute("toast", "Updated")
        }
    }
}
