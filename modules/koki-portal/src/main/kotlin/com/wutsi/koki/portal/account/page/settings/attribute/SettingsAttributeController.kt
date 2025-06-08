package com.wutsi.koki.portal.account.page.settings.attribute

import com.wutsi.koki.portal.account.service.AttributeService
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
@RequiresPermission(permissions = ["account:admin"])
class SettingsAttributeController(private val service: AttributeService) : AbstractPageController() {
    @GetMapping("/settings/accounts/attributes/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val attr = service.attribute(id)
        model.addAttribute("attr", attr)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ACCOUNT_SETTINGS_ATTRIBUTE,
                title = attr.name,
            )

        )
        return "accounts/settings/attributes/show"
    }
}
