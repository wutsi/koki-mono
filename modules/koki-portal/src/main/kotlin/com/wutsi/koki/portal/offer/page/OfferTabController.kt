package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.offer.model.OfferModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/offers/tab")
@RequiresPermission(["offer"])
class OfferTabController(
    private val listingService: ListingService
) : AbstractOfferController() {
    @GetMapping
    fun list(
        @RequestParam(name = "owner-id", required = false) ownerId: Long? = null,
        @RequestParam(name = "owner-type", required = false) ownerType: ObjectType? = null,
        @RequestParam(name = "read-only", required = false) readOnly: Boolean = false,
        @RequestParam(name = "test-mode", required = false) testMode: Boolean = false,
        model: Model
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("testMode", testMode)

        more(ownerId, ownerType, readOnly, model = model)
        return "offers/tab"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(name = "owner-id", required = false) ownerId: Long? = null,
        @RequestParam(name = "owner-type", required = false) ownerType: ObjectType? = null,
        @RequestParam(name = "read-only", required = false) readOnly: Boolean = false,
        @RequestParam(name = "limit", required = false) limit: Int = 20,
        @RequestParam(name = "offset", required = false) offset: Int = 0,
        model: Model
    ): String {
        val offers = findOffers(ownerId, ownerType, limit, offset)
        if (offers.isNotEmpty()) {
            model.addAttribute("offers", offers)
            model.addAttribute("moreUrl", buildMoreUrl(offers, ownerId, ownerType, limit, offset))
        }
        model.addAttribute("readOnly", readOnly)
        model.addAttribute("showOwner", false)
        return "offers/more"
    }

    private fun buildMoreUrl(
        offers: List<OfferModel>,
        ownerId: Long?,
        ownerType: ObjectType?,
        limit: Int,
        offset: Int
    ): String? {
        return if (offers.size < 20) {
            null
        } else {
            "/offers/tab/more?owner-id=$ownerId&owner-type=$ownerType&limit=$limit&offset=" + (offset + limit)
        }
    }

    private fun findOffers(
        ownerId: Long?,
        ownerType: ObjectType?,
        limit: Int,
        offset: Int,
    ): List<OfferModel> {
        return offerService.search(
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset,
        )
    }
}
