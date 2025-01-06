package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ListContactController(
    private val service: ContactService
) {
    @GetMapping("/contacts")
    fun list(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.CONTACT_LIST,
                title = "Contacts",
            )
        )
        more(true, limit, offset, model)
        return "contacts/list"
    }

    @GetMapping("/contacts/more")
    fun more(
        @RequestParam(required = false, name = "show-account") showAccount: Boolean = true,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val contacts = service.contacts(
            limit = limit,
            offset = offset
        )
        if (contacts.isNotEmpty()) {
            model.addAttribute("contacts", contacts)
            model.addAttribute("showAccount", showAccount)
            if (contacts.size >= limit) {
                val nextOffset = offset + limit
                var url = "/contacts/more?show-account=$showAccount&limit=$limit&offset=$nextOffset"
                model.addAttribute("moreUrl", url)
            }
        }

        return "contacts/more"
    }
}
