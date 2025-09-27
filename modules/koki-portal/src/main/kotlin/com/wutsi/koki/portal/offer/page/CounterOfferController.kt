package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.offer.form.OfferForm
import com.wutsi.koki.portal.offer.model.OfferModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/offers/counter")
@RequiresPermission(["offer:manage", "offer:full_access"])
class CounterOfferController : AbstractOfferStatusController() {
    @GetMapping
    fun counter(@RequestParam id: Long, model: Model): String {
        val offer = findSubmittedOffer(id)
        model.addAttribute("offer", offer)

        val user = userHolder.get()
        val form = toOfferForm(
            offer.copy(
                version = offer.version.copy(
                    submittingParty = if (offer.listing?.sellerAgentUser?.id == user?.id) {
                        OfferParty.SELLER
                    } else if (offer.listing?.buyerAgentUser?.id == user?.id) {
                        OfferParty.BUYER
                    } else {
                        OfferParty.UNKNOWN
                    }
                )
            )
        )
        return counter(form, model, offer)
    }

    private fun counter(form: OfferForm, model: Model, offer: OfferModel?): String {
        model.addAttribute("offer", offer ?: findOffer(form.id))
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER_COUNTER,
                title = getMessage("page.offer.counter.meta.title"),
            )
        )
        return "offers/counter"
    }

    @PostMapping
    fun submit(@ModelAttribute form: OfferForm, model: Model): String {
        try {
            offerVersionService.create(form)
            return "redirect:/offers/counter/done?id=${form.id}"
        } catch (ex: HttpClientErrorException) {
            loadError(ex, model)
            return counter(form, model, null)
        }
    }
}
