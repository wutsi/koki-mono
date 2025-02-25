package com.wutsi.koki.portal.user.page.settings.role

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.service.RoleService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/settings/roles")
@RequiresPermission(["security:admin"])
class SettingsRoleController(
    private val service: RoleService,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        val role = service.role(id)
        loadToast(id, referer, toast, timestamp, model)
        return show(role, model)
    }

    private fun show(role: RoleModel, model: Model): String {
        model.addAttribute("role", role)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_ROLE,
                title = role.title ?: role.name,
            )

        )
        return "users/settings/roles/show"
    }

    @GetMapping("/{id}/delete")
    fun delete(@PathVariable id: Long, model: Model): String {
        try {
            service.delete(id)
            return "redirect:/settings/roles?_op=del&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)

            val role = service.role(id)
            return show(role, model)
        }
    }

    private fun loadToast(
        id: Long,
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        model: Model
    ) {
        if (
            toast == id &&
            canShowToasts(timestamp, referer, listOf("/settings/roles/$id/edit", "/settings/roles/$id/permissions"))
        ) {
            model.addAttribute("toast", "Saved")
        }
    }
}
