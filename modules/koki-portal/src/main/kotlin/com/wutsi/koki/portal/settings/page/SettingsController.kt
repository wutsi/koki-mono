package com.wutsi.koki.portal.settings.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SettingsController : AbstractPageController() {
    @GetMapping("/settings")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SETTINGS,
                title = "Settings"
            )
        )
        return "settings/show"
    }
}
