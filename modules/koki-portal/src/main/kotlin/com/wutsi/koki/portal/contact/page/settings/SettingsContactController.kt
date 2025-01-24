package com.wutsi.koki.portal.contact.page.settings

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SettingsContactController : AbstractPageController() {
    @GetMapping("/settings/contacts")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.CONTACT_SETTINGS,
                title = "Contact Settings",
            )

        )
        return "contacts/settings/show"
    }
}
