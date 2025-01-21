package com.wutsi.koki.portal.account.page.settings.attribute

import com.wutsi.koki.portal.account.service.AttributeService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class SettingsAttributeController(private val service: AttributeService) : AbstractPageController() {
    @GetMapping("/settings/accounts/attributes/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val attr = service.attribute(id)
        model.addAttribute("attr", attr)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ACCOUNT_SETTINGS_ATTRIBUTE,
                title = attr.name,
            )

        )
        return "accounts/settings/attributes/show"
    }
}
