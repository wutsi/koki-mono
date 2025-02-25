package com.wutsi.koki.portal.user.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@RequiresPermission(["security:admin"])
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
