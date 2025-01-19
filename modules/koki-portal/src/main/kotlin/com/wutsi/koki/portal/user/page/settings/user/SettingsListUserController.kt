package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings/users")
class SettingsListUserController(
    private val service: UserService,
    private val httpRequest: HttpServletRequest,
) : AbstractPageController() {
    @GetMapping
    fun show(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false) created: Long? = null,
        @RequestParam(required = false) updated: Long? = null,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_USER_LIST,
                title = "Security Settings",
            )

        )
        loadCreatedToast(created, model)
        loadUpdatedToast(updated, model)
        more(limit, offset, model)

        return "users/settings/users/list"
    }

    private fun loadCreatedToast(created: Long?, model: Model) {
        val referer = httpRequest.getHeader(HttpHeaders.REFERER)
        if (created == null || referer?.endsWith("/settings/users/create") != true) {
            return
        }

        if (canLoadToast(created, "/settings/users/create")) {
            try {
                val user = service.user(created)
                model.addAttribute(
                    "toast",
                    "The user <a href='/settings/users/${user.id}'>${user.displayName}</a> has been created!"
                )
            } catch (ex: Exception) {
                // Ignore
            }
        }
    }

    private fun loadUpdatedToast(updated: Long?, model: Model) {
        if (canLoadToast(updated, "/settings/users/$updated/edit")) {
            try {
                val user = service.user(updated!!)
                model.addAttribute(
                    "toast",
                    "The user <a href='/settings/users/${user.id}'>${user.displayName}</a> has been updated!"
                )
            } catch (ex: Exception) {
                // Ignore
            }
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
        val users = service.users(
            limit = limit,
            offset = offset
        )
        model.addAttribute("users", users)
        if (users.size >= limit) {
            val nextOffset = offset + limit
            val moreUrl = "/settings/users/more?limit=$limit&offset=$nextOffset"
            model.addAttribute("moreUrl", moreUrl)
        }
        return "users/settings/users/more"
    }
}
