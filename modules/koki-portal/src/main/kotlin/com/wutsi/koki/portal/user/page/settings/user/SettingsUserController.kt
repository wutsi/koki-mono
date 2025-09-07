package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings/users")
@RequiresPermission(["security:admin"])
class SettingsUserController(
    private val service: UserService,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        val user = service.get(id)
        loadToast(id, referer, toast, timestamp, model)
        return show(user, model)
    }

    private fun show(user: UserModel, model: Model): String {
        model.addAttribute("me", user)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SECURITY_SETTINGS_USER,
                title = user.displayName ?: "-",
            )

        )
        return "users/settings/users/show"
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
            canShowToasts(timestamp, referer, listOf("/settings/users/$id/edit", "/settings/users/$id/roles"))
        ) {
            model.addAttribute("toast", "Saved")
        }
    }
}
