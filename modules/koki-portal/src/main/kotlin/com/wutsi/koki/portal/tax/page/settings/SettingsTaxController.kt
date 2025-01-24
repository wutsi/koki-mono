package com.wutsi.koki.portal.tax.page.settings

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SettingsTaxController : AbstractPageController() {
    @GetMapping("/settings/taxes")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.TAX_SETTINGS,
                title = "Taxes Settings",
            )

        )
        return "taxes/settings/show"
    }
}
