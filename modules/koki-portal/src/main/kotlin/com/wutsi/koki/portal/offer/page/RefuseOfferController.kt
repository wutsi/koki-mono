package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.offer.form.OfferForm
import com.wutsi.koki.portal.security.RequiresPermission
import io.lettuce.core.KillArgs.Builder.id
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/offers/refuse")
@RequiresPermission(["offer:manage", "offer:full_access"])
class RefuseOfferController : AbstractEditOfferController() {
    @GetMapping
    fun refuse(@RequestParam id: Long, model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER_REFUSE,
                title = getMessage("page.offer.show.meta.title", arrayOf(id)),
            )
        )
        return refuse(OfferForm(id = id), model)
    }

    private fun refuse(form: OfferForm, model: Model): String {
        val offer = findOffer(form.id)
        model.addAttribute("offer", offer)
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER_REFUSE,
                title = getMessage("page.offer.show.meta.title", arrayOf(offer.id)),
            )
        )
        return "offers/refuse"
    }

    @PostMapping
    fun submit(@ModelAttribute form: OfferForm, model: Model): String {
        try {
            offerService.updateStatus(form.copy(status = OfferStatus.REJECTED))
            return "redirect:/offers/refuse/done?id=${form.id}"
        } catch (ex: HttpClientErrorException) {
            loadError(ex, model)
            return refuse(form, model)
        }
    }
}
