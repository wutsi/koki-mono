package com.wutsi.koki.portal.pub.whatsapp.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.pub.agent.service.AgentService
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.tracking.form.TrackForm
import com.wutsi.koki.portal.pub.tracking.service.TrackService
import com.wutsi.koki.portal.pub.user.model.UserModel
import com.wutsi.koki.track.dto.TrackEvent
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder
import java.util.UUID

@Controller
@RequestMapping("/whatsapp")
class WhatsappController(
    private val request: HttpServletRequest,
    private val listingService: ListingService,
    private val agentService: AgentService,
    private val trackService: TrackService,
) : AbstractPageController() {
    @GetMapping("/listings/{id}")
    fun listing(@PathVariable id: Long): String {
        val listing = listingService.get(id, false)
        track(id, ObjectType.LISTING, listing.sellerAgentUser?.id)

        /* Format: Villa a louer - 2 chambres, 1 salle de bain, 500m2 - 300000 XAF - https://koki.com/listings/12345 */
        val details = listOfNotNull(

            listOf(
                getMessage("property-type.${listing.propertyType}"),
                getMessage("page.whatsapp.${listing.listingType}").lowercase(),
            ).joinToString(separator = " "),

            listing.address?.toText(false),

            listOfNotNull(
                listing.bedrooms?.let { rooms -> rooms.toString() + " " + getMessage("page.listing.bedrooms") },
                listing.bedrooms?.let { rooms -> rooms.toString() + " " + getMessage("page.listing.bathrooms") },
                listing.lotArea?.let { area -> area.toString() + " " + "m2" }
            ).joinToString(separator = ", ").ifEmpty { null },

            listing.price?.displayText,

            listing.publicUrl,
        ).joinToString(separator = " - ")

        val text = getMessage("page.whatsapp.listing.text", arrayOf(listing.sellerAgentUser?.firstName ?: "", details))
        val url = toWhatsappUrl(listing.sellerAgentUser, text)

        return "redirect:$url"
    }

    @GetMapping("/agents/{id}")
    fun agent(@PathVariable id: Long): String {
        val agent = agentService.get(id)
        track(id, ObjectType.AGENT, agent.user.id)

        val text = getMessage("page.whatsapp.agent.text", arrayOf(agent.user.firstName))
        val url = toWhatsappUrl(agent.user, text)

        return "redirect:$url"
    }

    @GetMapping("/neighbourhoods/{id}")
    fun place(@PathVariable id: Long, @RequestParam(name = "agent-id") agentId: Long): String {
        val agent = agentService.get(agentId)
        track(id, ObjectType.PLACE, agent.user.id)

        val text = getMessage("page.whatsapp.neighbourhood.text", arrayOf(agent.user.firstName))
        val url = toWhatsappUrl(agent.user, text)

        return "redirect:$url"
    }

    private fun toWhatsappUrl(user: UserModel?, text: String): String {
        val xphone = user?.mobile?.trimStart('+') ?: ""
        return "https://wa.me/$xphone?text=" + URLEncoder.encode(text, "UTF-8")
    }

    private fun track(productId: Long, productType: ObjectType, userId: Long?) {
        trackService.track(
            TrackForm(
                productId = productId.toString(),
                productType = productType,
                event = TrackEvent.MESSAGE,
                url = request.requestURL.toString(),
                ua = request.getHeader("User-Agent") ?: "",
                referrer = request.getHeader("Referer") ?: "",
                time = System.currentTimeMillis(),
                hitId = UUID.randomUUID().toString(),
                page = PageName.WHATSAPP,
                value = userId?.let { "user:$userId" },
            )
        )
    }
}
