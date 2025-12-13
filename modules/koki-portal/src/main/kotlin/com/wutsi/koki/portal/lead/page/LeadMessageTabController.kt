package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.lead.service.LeadMessageService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/leads/messages/tab")
class LeadMessageTabController(private val service: LeadMessageService) : AbstractLeadMessageController() {
    @GetMapping
    fun tab(
        @RequestParam("owner-id") ownerId: Long,
        @RequestParam("owner-type") ownerType: ObjectType,
        @RequestParam("test-mode", required = false) testMode: Boolean = false,
        model: Model,
    ): String {
        model.addAttribute("testMode", testMode)
        more(ownerId, ownerType, model = model)
        return "leads/messages/tab"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam("owner-id") ownerId: Long,
        @RequestParam("owner-type") ownerType: ObjectType,
        @RequestParam("limit") limit: Int = 20,
        @RequestParam("offset") offet: Int = 0,
        model: Model,
    ): String {
        val messages = if (ownerType == ObjectType.LEAD) {
            service.search(
                leadIds = listOf(ownerId),
                limit = limit,
                offset = offet,
            )
        } else {
            emptyList()
        }

        if (messages.isNotEmpty()) {
            model.addAttribute("messages", messages)

            if (messages.size >= limit) {
                model.addAttribute(
                    "moreUrl",
                    "/leads/messages/tab/more?owner-id=$ownerId&owner-type=$ownerType&limit=$limit&offset=${offet + limit}"
                )
            }
        }

        return "leads/messages/more"
    }
}
