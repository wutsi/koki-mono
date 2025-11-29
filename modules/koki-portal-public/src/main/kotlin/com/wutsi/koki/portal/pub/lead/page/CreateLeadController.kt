package com.wutsi.koki.portal.pub.lead.page

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
) : AbstractPageController() {
    @GetMapping
    fun create(@RequestParam("listing-id") listingId: Long, model: Model): String {
        val listing = listingService.get(listingId)
        model.addAttribute("listing", listing)

        val user = userHolder.get()
        model.addAttribute("user", user)

        model.addAttribute(
            "form",
            LeadForm(
                listingId = listingId,
                phone = user?.mobile ?: "",
                email = user?.email ?: "",
                country = user?.country ?: tenantHolder.get()?.country,
                firstName = user?.firstName ?: "",
                lastName = user?.lastName ?: "",
                message = getMessage("page.lead.message-default"),
                publicUrl = listing.publicUrl,
            )
        )
        return "leads/create"
    }

    @PostMapping
    fun send(@ModelAttribute form: LeadForm): String {
        service.create(form)

        val redirectUrl = form.publicUrl ?: "/listings/${form.listingId}"
        val toast = ListingController.TOAST_MESSAGE_SENT
        return "redirect:$redirectUrl?_toast=$toast&_ts=" + System.currentTimeMillis()
    }
}
