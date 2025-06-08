package com.wutsi.koki.portal.account.page.settings

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@RequiresPermission(permissions = ["account:admin"])
class SettingsAccountController : AbstractPageController() {
    @GetMapping("/settings/accounts")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ACCOUNT_SETTINGS,
                title = "Account Settings",
            )

        )
        return "accounts/settings/show"
    }
}
