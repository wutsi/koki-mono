package com.wutsi.koki.portal.email.page.settings.smtp

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SettingsEmailController : AbstractPageController() {
    @GetMapping("/settings/email")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.EMAIL_SETTINGS,
                title = "Email Settings",
            )

        )
        return "emails/settings/show"
    }
}
