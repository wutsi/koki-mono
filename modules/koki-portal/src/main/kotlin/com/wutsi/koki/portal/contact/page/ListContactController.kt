package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.CurrentUserHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ListContactController(
    private val service: ContactService,
    private val currentUser: CurrentUserHolder,
) : AbstractPageController() {
    companion object {
        const val COL_ALL = "1"
        const val COL_CREATED = "2"
    }

    @GetMapping("/contacts")
    fun list(
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("collection", toCollection(collection))
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.CONTACT_LIST,
                title = "Contacts",
            )
        )
        more(collection, true, limit, offset, model)
        return "contacts/list"
    }

    @GetMapping("/contacts/more")
    fun more(
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false, name = "show-account") showAccount: Boolean = true,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val col = toCollection(collection)
        val userId = currentUser.id()
        val contacts = service.contacts(
            createdByIds = if (col == COL_CREATED) {
                userId?.let { id -> listOf(id) } ?: emptyList()
            } else {
                emptyList()
            },
            limit = limit,
            offset = offset
        )
        if (contacts.isNotEmpty()) {
            model.addAttribute("contacts", contacts)
            model.addAttribute("showAccount", showAccount)
            if (contacts.size >= limit) {
                val nextOffset = offset + limit
                var url = "/contacts/more?show-account=$showAccount&limit=$limit&offset=$nextOffset"
                if (collection != null) {
                    url = "$url&col=$collection"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "contacts/more"
    }

    private fun toCollection(collection: String?): String {
        return when (collection) {
            COL_ALL -> COL_ALL
            COL_CREATED -> COL_CREATED
            else -> COL_ALL
        }
    }
}
