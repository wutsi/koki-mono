package com.wutsi.koki.portal.pub.lead.page

import com.wutsi.koki.portal.pub.agent.service.AgentService
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.lead.form.LeadForm
import com.wutsi.koki.portal.pub.lead.service.LeadService
import com.wutsi.koki.portal.pub.listing.page.ListingController
import com.wutsi.koki.portal.pub.listing.service.ListingService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/leads/create")
class CreateLeadController(
    private val service: LeadService,
    private val listingService: ListingService,
    private val agentService: AgentService,
) : AbstractPageController() {
    @GetMapping
    fun create(
        @RequestParam("listing-id", required = false) listingId: Long? = null,
        @RequestParam("agent-id", required = false) agentId: Long? = null,
        model: Model,
    ): String {
        val listing = listingId?.let { id -> listingService.get(id) }
        model.addAttribute("listing", listing)

        val agent = agentId?.let { id -> agentService.get(id) }
        model.addAttribute("agent", agent)
        model.addAttribute("agentUser", agent?.user ?: listing?.sellerAgentUser)

        val user = userHolder.get()
        model.addAttribute(
            "form",
            LeadForm(
                listingId = listingId,
                agentUserId = agent?.user?.id,
                phone = user?.mobile ?: "",
                email = user?.email ?: "",
                country = user?.country ?: tenantHolder.get().country,
                firstName = user?.firstName ?: "",
                lastName = user?.lastName ?: "",
                message = getMessage(
                    listingId?.let { "page.lead.listing-message-default" } ?: "page.lead.agent-message-default"
                ),
                publicUrl = listing?.publicUrl ?: agent?.publicUrl ?: "/",
            )
        )
        return "leads/create"
    }

    @PostMapping
    fun send(@ModelAttribute form: LeadForm): String {
        service.create(form)

        val redirectUrl = form.publicUrl
        val toast = ListingController.TOAST_MESSAGE_SENT
        return "redirect:$redirectUrl?_toast=$toast&_ts=" + System.currentTimeMillis()
    }
}
