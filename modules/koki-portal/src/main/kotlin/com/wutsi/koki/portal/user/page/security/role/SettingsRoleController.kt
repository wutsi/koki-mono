package com.wutsi.koki.portal.user.page.security

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.model.RoleForm
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.service.RoleService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/settings/security/roles")
class SettingsEditRoleController(
    private val service: RoleService
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val role = service.role(id)
        return show(role, model)
    }

    private fun show(role: RoleModel, model: Model): String {
        model.addAttribute("role", role)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_ROLE,
                title = role.title,
            )

        )
        return "users/settings/roles/show"
    }

    @PostMapping("/{id}/delete")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: RoleForm,
        model: Model
    ): String {
        try {
            service.delete(id)
            return "redirect:/settings/security/roles?deleted=$id"
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)

            val role = service.role(id)
            return show(role, model)
        }
    }
}
