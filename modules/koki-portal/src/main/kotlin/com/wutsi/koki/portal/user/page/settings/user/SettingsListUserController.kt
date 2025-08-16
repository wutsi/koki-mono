package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.koki.portal.account.page.ListAccountController
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.form.SearchUserForm
import com.wutsi.koki.portal.user.service.RoleService
import com.wutsi.koki.portal.user.service.UserService
import io.lettuce.core.KillArgs.Builder.user
import io.micrometer.core.instrument.Metrics.more
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings/users")
@RequiresPermission(["security:admin"])
class SettingsListUserController(
    private val service: UserService,
    private val roleService: RoleService,
) : AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListAccountController::class.java)
    }

    @GetMapping
    fun show(
        @ModelAttribute form: SearchUserForm,
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SECURITY_SETTINGS_USER_LIST,
                title = "Security Settings",
            )
        )

        loadToast(referer, toast, timestamp, operation, model)
        more(form, 20, 0, model)
        return "users/settings/users/list"
    }

    @GetMapping("/more")
    fun more(
        @ModelAttribute form: SearchUserForm,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("form", form)

        if (!form.keyword.isNullOrEmpty()) {
            val users = service.users(
                keyword = form.keyword,
                limit = limit,
                offset = offset
            )
            if (!users.isEmpty()) {
                model.addAttribute("users", users)
            }

            if (users.size >= limit) {
                val nextOffset = offset + limit
                var moreUrl = "/settings/users/more?limit=$limit&offset=$nextOffset&keyword=${form.keyword}"
                model.addAttribute("moreUrl", moreUrl)
            }
        }
        return "users/settings/users/more"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        operation: String?,
        model: Model
    ) {
        if (
            toast != null &&
            canShowToasts(timestamp, referer, listOf("/settings/users/$toast", "/settings/users/create"))
        ) {
            if (operation == "del") {
                model.addAttribute("toast", "Deleted")
            } else {
                try {
                    val user = service.user(toast, fullGraph = false)
                    model.addAttribute(
                        "toast",
                        "<a href='/settings/users/${user.id}'>${user.displayName}</a> has been saved!"
                    )
                } catch (ex: Exception) { // I
                    LOGGER.warn("Unable to load toast information for AccountUser#$toast", ex)
                }
            }
        }
    }
}
