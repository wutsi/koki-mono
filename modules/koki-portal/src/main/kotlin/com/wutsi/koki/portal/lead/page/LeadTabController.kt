package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.lead.service.LeadService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/leads/tab")
@RequiresPermission(["lead", "lead:full_access"])
class LeadTabController(private val service: LeadService) : AbstractLeadController() {
    @GetMapping
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        model: Model,
    ): String {
        model.addAttribute("testMode", testMode)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        more(ownerId, ownerType, model = model)
        return "leads/tab"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)

        val leads = if (ownerType == ObjectType.LISTING) {
            service.search(
                listingIds = listOf(ownerId),
                limit = limit,
                offset = offset,
            )
        } else {
            emptyList()
        }

        if (leads.isNotEmpty()) {
            model.addAttribute("leads", leads)
            if (leads.size >= limit) {
                model.addAttribute(
                    "moreUrl",
                    "/leads/more?owner-id=$ownerId&owner-type=$ownerType&limit=$limit&offset=" + (offset + limit),
                )
            }
        }
        return "leads/more"
    }
}
