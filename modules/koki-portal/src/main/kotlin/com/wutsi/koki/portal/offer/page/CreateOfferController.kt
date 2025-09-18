package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.offer.form.OfferForm
import com.wutsi.koki.portal.security.RequiresPermission
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.text.SimpleDateFormat
import java.util.Date

@Controller
@RequestMapping("/offers/create")
@RequiresPermission(["offer:manage", "offer:full_access"])
class CreateOfferController(
    private val listingService: ListingService,
) : AbstractEditOfferController() {
    @GetMapping
    fun create(
        @RequestParam(name = "listing-id", required = false) listingId: Long,
        model: Model
    ): String {
        val listing = listingService.get(listingId)
        model.addAttribute("listing", listing)

        val df = SimpleDateFormat("yyyy-MM-dd")
        val form = OfferForm(
            ownerId = listingId,
            ownerType = ObjectType.LISTING,
            price = listing.price?.amount?.toLong(),
            currency = tenantHolder.get()?.currency,
            pricePerMonth = listing.listingTypeRental,
            expiresAtMin = df.format(DateUtils.addDays(Date(), 1)),
            submittingParty = OfferParty.BUYER,
            sellerAgentUserId = listing.sellerAgentUser?.id ?: -1,
            buyerAgentUserId = getUser()?.id ?: -1,
        )
        return create(form, model)
    }

    fun create(form: OfferForm, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER_CREATE,
                title = getMessage("page.offer.create.meta.title"),
            )
        )
        return "offers/create"
    }

    @PostMapping
    fun submit(form: OfferForm, model: Model): String {
        try {
            val id = offerService.create(form)
            return "redirect:/offers/create/done?id=$id"
        } catch (ex: HttpClientErrorException) {
            loadError(ex, model)
            return create(form, model)
        }
    }
}
