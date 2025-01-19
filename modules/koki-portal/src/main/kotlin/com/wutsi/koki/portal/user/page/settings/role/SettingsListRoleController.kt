package com.wutsi.koki.portal.user.page.settings.role

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.service.RoleService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings/roles")
class SettingsListRoleController(
    private val service: RoleService,
    private val httpRequest: HttpServletRequest,
) : AbstractPageController() {
    @GetMapping
    fun show(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false) created: Long? = null,
        @RequestParam(required = false) updated: Long? = null,
        @RequestParam(required = false) deleted: Long? = null,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_ROLE_LIST,
                title = "Security Settings",
            )

        )
        loadCreatedToast(created, model)
        loadUpdatedToast(updated, model)
        loadDeletedToast(deleted, model)
        more(limit, offset, model)

        return "users/settings/roles/list"
    }

    private fun loadCreatedToast(created: Long?, model: Model) {
        val referer = httpRequest.getHeader(HttpHeaders.REFERER)
        if (created == null || referer?.endsWith("/settings/roles/create") != true) {
            return
        }

        if (canLoadToast(created, "/settings/roles/create")) {
            try {
                val role = service.role(created)
                model.addAttribute(
                    "toast",
                    "The role <a href='/settings/roles/${role.id}'>${role.name}</a> has been created!"
                )
            } catch (ex: Exception) {
                // Ignore
            }
        }
    }

    private fun loadUpdatedToast(updated: Long?, model: Model) {
        if (canLoadToast(updated, "/settings/roles/$updated/edit")) {
            try {
                val role = service.role(updated!!)
                model.addAttribute(
                    "toast",
                    "The role <a href='/settings/roles/${role.id}'>${role.name}</a> has been updated!"
                )
            } catch (ex: Exception) {
                // Ignore
            }
        }
    }

    private fun loadDeletedToast(deleted: Long?, model: Model) {
        if (canLoadToast(deleted, "/settings/roles/$deleted")) {
            model.addAttribute("toast", "The role has been deleted!")
        }
    }

    private fun canLoadToast(id: Long?, refererSuffix: String): Boolean {
        return id != null &&
            httpRequest.getHeader(HttpHeaders.REFERER)?.endsWith(refererSuffix) == true
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val roles = service.roles(
            limit = limit,
            offset = offset
        )
        model.addAttribute("roles", roles)
        if (roles.size >= limit) {
            val nextOffset = offset + limit
            val moreUrl = "/settings/roles/more?limit=$limit&offset=$nextOffset"
            model.addAttribute("moreUrl", moreUrl)
        }
        return "users/settings/roles/more"
    }
}
