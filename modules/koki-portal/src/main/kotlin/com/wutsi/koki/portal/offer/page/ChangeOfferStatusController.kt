package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.offer.form.OfferForm
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.text.SimpleDateFormat
import java.util.Date

@Controller
@RequestMapping("/offers/status")
@RequiresPermission(["offer:manage", "offer:full_access"])
class ChangeOfferStatusController : AbstractEditOfferController() {
    @GetMapping
    fun status(
        @RequestParam id: Long,
        @RequestParam status: OfferStatus,
        model: Model
    ): String {
        val df = SimpleDateFormat("yyyy-MM-dd")
        return status(
            form = OfferForm(id = id, status = status, closedAtMax = df.format(Date())),
            model = model,
        )
    }

    private fun status(form: OfferForm, model: Model): String {
        val offer = when (form.status) {
            OfferStatus.REJECTED, OfferStatus.ACCEPTED -> {
                val tmp = findOffer(form.id)
                if (!tmp.statusSubmitted || !tmp.canAcceptOrRejectOrCounter(getUser())) {
                    throw HttpClientErrorException(HttpStatusCode.valueOf(403))
                }
                tmp
            }

            OfferStatus.CLOSED, OfferStatus.CANCELLED -> {
                val tmp = findOffer(form.id)
                if (!(tmp.statusAccepted || tmp.statusRejected) || !tmp.canCloseOrCancel(getUser())) {
                    throw HttpClientErrorException(HttpStatusCode.valueOf(403))
                }
                tmp
            }

            else -> findOffer(form.id)
        }
        model.addAttribute("offer", offer)
        model.addAttribute("form", form)

        val status = form.status?.name?.lowercase()
        model.addAttribute("title", getMessage("page.offer.status.$status.title"))
        model.addAttribute("description", getMessage("page.offer.status.$status.description"))
        model.addAttribute("confirmation", getMessage("page.offer.status.$status.confirmation"))

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER_STATUS,
                title = getMessage("page.offer.status.$status.meta.title"),
            )
        )
        return "offers/status"
    }

    @PostMapping
    fun submit(@ModelAttribute form: OfferForm, model: Model): String {
        try {
            offerService.updateStatus(form)
            return "redirect:/offers/status/done?id=${form.id}"
        } catch (ex: HttpClientErrorException) {
            loadError(ex, model)
            return status(form, model)
        }
    }
}
