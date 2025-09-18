package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.offer.model.OfferModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/offers")
@RequiresPermission(["offer"])
class ListOfferController : AbstractOfferController() {
    @GetMapping
    fun list(model: Model): String {
        more(model = model)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER_LIST,
                title = getMessage("page.offer.list.meta.title"),
            )
        )
        return "offers/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(name = "limit", required = false) limit: Int = 20,
        @RequestParam(name = "offset", required = false) offset: Int = 0,
        model: Model
    ): String {
        val offer = findOffers(limit, offset)
        model.addAttribute("offer", offer)

        val offers = findOffers(limit, offset)
        model.addAttribute("offers", offers)
        model.addAttribute("showOwner", true)
        model.addAttribute("moreUrl", buildMoreUrl(offers, limit, offset))
        return "offers/more"
    }

    private fun buildMoreUrl(offers: List<OfferModel>, limit: Int, offset: Int): String? {
        return if (offers.size < 20) {
            null
        } else {
            "/offers/more?limit=$limit&offset=" + (offset + limit)
        }
    }

    private fun findOffers(limit: Int, offset: Int): List<OfferModel> {
        val user = userHolder.get()
        return offerService.search(
            agentUserId = if (user?.hasFullAccess("offer") == true) null else user?.id,
            limit = limit,
            offset = offset,
        )
    }
}
