package com.wutsi.koki.portal.contact.page.settings.type

import com.wutsi.koki.portal.contact.service.ContactTypeService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class SettingsContactTypeController(private val service: ContactTypeService) : AbstractPageController() {
    @GetMapping("/settings/contacts/types/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val type = service.contactType(id)
        model.addAttribute("type", type)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.CONTACT_SETTINGS_TYPE,
                title = type.name,
            )

        )
        return "contacts/settings/types/show"
    }
}
