package com.wutsi.koki.portal.pub.qrcode.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.pub.agent.service.AgentService
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.tracking.form.TrackForm
import com.wutsi.koki.portal.pub.tracking.service.TrackService
import com.wutsi.koki.track.dto.TrackEvent
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller
@RequestMapping("/qr-codes")
class QrCodeController(
    private val agentService: AgentService,
    private val listingService: ListingService,
    private val trackService: TrackService,
    private val request: HttpServletRequest,
) {
    @GetMapping("/agents/{id}")
    fun agent(@PathVariable id: Long, model: Model): String {
        track(id, ObjectType.AGENT)
        val agent = agentService.get(id, false)
        return "redirect:${agent.publicUrl}"
    }

    @GetMapping("/listings/{id}")
    fun listing(@PathVariable id: Long, model: Model): String {
        track(id, ObjectType.LISTING)
        val listing = listingService.get(id, false)
        return listing.publicUrl?.let { url -> "redirect:$url" } ?: "redirect:/listings/$id"
    }

    private fun track(productId: Long, productType: ObjectType) {
        trackService.track(
            TrackForm(
                productId = productId.toString(),
                productType = productType,
                event = TrackEvent.QR_CODE_SCAN,
                url = request.requestURL.toString(),
                ua = request.getHeader("User-Agent") ?: "",
                referrer = request.getHeader("Referer") ?: "",
                time = System.currentTimeMillis(),
                hitId = UUID.randomUUID().toString(),
                page = PageName.QR,
            )
        )
    }
}
