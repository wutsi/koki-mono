package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.contact.service.ContactService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/contacts/widgets/list")
class ListContactWidgetController(
    private val service: ContactService
) {
    @GetMapping
    fun list(
        @RequestParam(required = false, name = "account-id") accountId: Long? = null,
        @RequestParam(required = false, name = "show-account") showAccount: Boolean = true,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("accountId", accountId)
        more(accountId, showAccount, limit, offset, model)
        return "contacts/widgets/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false, name = "account-id") accountId: Long? = null,
        @RequestParam(required = false, name = "show-account") showAccount: Boolean = true,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val contacts = service.contacts(
            accountIds = accountId?.let { id -> listOf(id) } ?: emptyList(),
            limit = limit,
            offset = offset
        )
        if (contacts.isNotEmpty()) {
            model.addAttribute("contacts", contacts)
            model.addAttribute("showAccount", showAccount)
            if (contacts.size >= limit) {
                val nextOffset = offset + limit
                var url = "/contacts/widgets/list/more?show-account=$showAccount&limit=$limit&offset=$nextOffset"
                if (accountId != null) {
                    url = "$url&account-id=$accountId"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "contacts/more"
    }
}
