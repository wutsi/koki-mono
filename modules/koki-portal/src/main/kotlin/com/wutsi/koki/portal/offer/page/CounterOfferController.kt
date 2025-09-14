package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.offer.form.OfferForm
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/offers/counter")
@RequiresPermission(["offer:manage", "offer:full_access"])
class CounterOfferController : AbstractEditOfferController() {
    @GetMapping
    fun accept(@RequestParam id: Long, model: Model): String {
        val offer = findOffer(id)
        model.addAttribute("offer", offer)

        val form = toOfferForm(offer)
        model.addAttribute(
            "form",
            form.copy(
                submittingParty = if (form.submittingParty == OfferParty.BUYER) OfferParty.SELLER else OfferParty.BUYER
            )
        )

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
        return "redirect:/offers/counter/done?id=${form.id}"
    }
}
