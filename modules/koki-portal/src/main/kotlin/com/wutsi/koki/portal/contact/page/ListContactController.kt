package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["contact"])
class ListContactController(
    private val service: ContactService,
    private val typeService: TypeService,
    private val currentUser: CurrentUserHolder,
) : AbstractContactController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListContactController::class.java)

        const val COL_ALL = "1"
        const val COL_CREATED = "2"
    }

    @GetMapping("/contacts")
    fun list(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false, name = "type-id") typeId: Long? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
        model: Model
    ): String {
        model.addAttribute("collection", toCollection(collection))
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.CONTACT_LIST,
                title = "Contacts",
            )
        )
        loadToast(referer, toast, timestamp, operation, model)
        more(collection, typeId, true, limit, offset, model)

        model.addAttribute(
            "types",
            typeService.types(objectType = ObjectType.CONTACT, active = true, limit = Integer.MAX_VALUE)
        )
        model.addAttribute("typeId", typeId)

        return "contacts/list"
    }

    @GetMapping("/contacts/more")
    fun more(
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false, name = "type-id") typeId: Long? = null,
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
            contactTypeIds = typeId?.let { listOf(typeId) } ?: emptyList(),
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
                if (typeId != null) {
                    url = "$url&type-id=$typeId"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "contacts/more"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        operation: String?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/contacts/$toast", "/contacts/create"))) {
            if (operation == "del") {
                model.addAttribute("toast", "Deleted")
            } else {
                try {
                    val contact = service.contact(toast, fullGraph = false)
                    model.addAttribute(
                        "toast",
                        "<a href='/contacts/${contact.id}'>${contact.name}</a> has been saved!"
                    )
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to load toast information for Contact#$toast", ex)
                }
            }
        }
    }

    private fun toCollection(collection: String?): String {
        return when (collection) {
            COL_ALL -> COL_ALL
            COL_CREATED -> COL_CREATED
            else -> COL_ALL
        }
    }
}
