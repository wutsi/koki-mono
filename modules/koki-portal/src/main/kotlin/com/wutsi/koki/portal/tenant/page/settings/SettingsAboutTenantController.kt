package com.wutsi.koki.portal.tenant.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@RequiresPermission(["tenant:admin"])
class SettingsAboutTenantController : AbstractPageController() {
    @GetMapping("/settings/tenant/about")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.TENANT_SETTINGS_ABOUT,
                title = "About",
            )

        )
        return "tenant/settings/about"
    }
}
