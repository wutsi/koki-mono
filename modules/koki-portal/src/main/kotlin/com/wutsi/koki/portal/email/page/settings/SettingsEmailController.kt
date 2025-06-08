package com.wutsi.koki.portal.email.page.settings.smtp

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@RequiresPermission(["email:admin"])
class SettingsEmailController : AbstractPageController() {
    @GetMapping("/settings/email")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.EMAIL_SETTINGS,
                title = "Email Settings",
            )

        )
        return "emails/settings/show"
    }
}
