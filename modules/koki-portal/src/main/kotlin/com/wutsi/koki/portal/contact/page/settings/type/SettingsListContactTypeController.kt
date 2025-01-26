package com.wutsi.koki.portal.contact.page.settings.type

import com.wutsi.koki.portal.contact.service.ContactTypeService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["contact:admin"])
class SettingsListContactTypeController(private val service: ContactTypeService) : AbstractPageController() {
    @GetMapping("/settings/contacts/types")
    fun list(
        model: Model
    ): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.CONTACT_SETTINGS_TYPE_LIST,
                title = "Contact Types",
            )

        )
        more(model = model)
        return "contacts/settings/types/list"
    }

    @GetMapping("/settings/contacts/types/more")
    fun more(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val types = service.contactTypes(limit = limit, offset = offset)
        model.addAttribute("types", types)
        if (types.size >= limit) {
            val nextOffset = offset + limit
            val moreUrl = "/settings/contacts/types/more?limit=$limit&offset=$nextOffset"
            model.addAttribute("moreUrl", moreUrl)
        }
        return "contacts/settings/types/more"
    }
}
