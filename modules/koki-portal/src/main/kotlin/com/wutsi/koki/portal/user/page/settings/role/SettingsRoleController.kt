package com.wutsi.koki.portal.user.page.settings.role

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.service.RoleService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/settings/roles")
class SettingsRoleController(
    private val service: RoleService,
    private val httpRequest: HttpServletRequest,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        @RequestParam(required = false) updated: Long? = null,
        model: Model
    ): String {
        val role = service.role(id)
        loadUpdatedToast(updated, model)
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

    private fun loadUpdatedToast(updated: Long?, model: Model) {
        val referer = httpRequest.getHeader(HttpHeaders.REFERER)
        if (
            updated != null &&
            referer != null &&
            (
                referer.endsWith("/settings/roles/$updated/edit") ||
                    referer.endsWith("/settings/roles/$updated/permissions")
                )
        ) {
            model.addAttribute("toast", "Updated")
        }
    }

    @GetMapping("/{id}/delete")
    fun delete(@PathVariable id: Long, model: Model): String {
        try {
            service.delete(id)
            return "redirect:/settings/roles?deleted=$id"
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)

            val role = service.role(id)
            return show(role, model)
        }
    }
}
