package com.wutsi.koki.portal.offer.page

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
@RequestMapping("/offers/accept")
@RequiresPermission(["offer:manage", "offer:full_access"])
class AcceptOfferController : AbstractEditOfferController() {
    @GetMapping
    fun accept(@RequestParam id: Long, model: Model): String {
        val offer = findOffer(id)
        model.addAttribute("offer", offer)
        model.addAttribute("form", toOfferForm(offer))

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER_ACCEPT,
                title = getMessage("page.offer.accept.meta.title"),
            )
        )
        return "offers/accept"
    }

    @PostMapping
    fun submit(@ModelAttribute form: OfferForm, model: Model): String {
        return "redirect:/offers/accept/done?id=${form.id}"
    }
}
