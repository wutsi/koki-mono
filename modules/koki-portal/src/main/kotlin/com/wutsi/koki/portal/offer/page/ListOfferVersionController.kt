package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.portal.offer.model.OfferVersionModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/offer-versions")
@RequiresPermission(["offer"])
class ListOfferVersionController : AbstractOfferDetailsController() {
    @GetMapping
    fun list(
        @RequestParam(name = "offer-id") offerId: Long,
        @RequestParam(name = "test-mode", required = false) testMode: Boolean = false,
        model: Model
    ): String {
        model.addAttribute("testMode", testMode)

        more(offerId, model = model)
        return "offers/versions/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(name = "offer-id") offerId: Long,
        @RequestParam(name = "limit", required = false) limit: Int = 20,
        @RequestParam(name = "offset", required = false) offset: Int = 0,
        model: Model
    ): String {
        val offer = findOffer(offerId)
        model.addAttribute("offer", offer)

        val versions = findVersions(offerId, limit, offset)
        model.addAttribute("versions", versions)
        model.addAttribute("moreUrl", buildMoreUrl(versions, offerId, limit, offset))
        return "offers/versions/more"
    }

    private fun buildMoreUrl(versions: List<OfferVersionModel>, offerId: Long, limit: Int, offset: Int): String? {
        return if (versions.size < 20) {
            null
        } else {
            "/offer-versions/more?offer-id=$offerId&limit=$limit&offset=" + (offset + limit)
        }
    }

    private fun findVersions(offerId: Long, limit: Int, offset: Int): List<OfferVersionModel> {
        val user = userHolder.get()
        return offerVersionService.search(
            offerId = offerId,
            agentUserId = if (user?.hasFullAccess("offer") == true) null else user?.id,
            limit = limit,
            offset = offset,
        )
    }
}
