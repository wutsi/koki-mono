package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/contacts/tab")
@RequiresPermission(["contact"])
class ContactTabController(
    private val service: ContactService,
    private val typeService: TypeService,
) : AbstractPageController() {
    @GetMapping
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(name = "test-mode", required = false) testMode: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("testMode", testMode)

        if (ownerType == ObjectType.ACCOUNT) {
            model.addAttribute("addUrl", "/contacts/create?account-id=$ownerId")
        }

        more(ownerId, ownerType, limit, offset, model)
        return "contacts/tab"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val accountId = if (ownerType == ObjectType.ACCOUNT) {
            ownerId
        } else {
            null
        }

        model.addAttribute(
            "types",
            typeService.types(objectType = ObjectType.CONTACT, active = true, limit = Integer.MAX_VALUE)
        )

        if (accountId != null) {
            val contacts = service.contacts(
                accountIds = listOf(accountId),
                limit = limit,
                offset = offset
            )
            if (contacts.isNotEmpty()) {
                model.addAttribute("contacts", contacts)
                model.addAttribute("showAccount", false)
                if (contacts.size >= limit) {
                    val nextOffset = offset + limit
                    var url =
                        "/contacts/tab/more?owner-id=$ownerId&owner-type=$ownerType&limit=$limit&offset=$nextOffset"
                    model.addAttribute("moreUrl", url)
                }
            }
        }
        return "contacts/more"
    }
}
