package com.wutsi.koki.portal.user.page.security

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SettingsSecurityController : AbstractPageController() {
    @GetMapping("/settings/security")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS,
                title = "Security Settings",
            )

        )
        return "users/settings/show"
    }
}
