package com.wutsi.koki.portal.pub.whatsapp.service

import com.wutsi.koki.portal.pub.agent.model.AgentModel
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.place.model.PlaceModel
import com.wutsi.koki.portal.pub.user.model.UserModel
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.util.Locale

@Service
class WhatsappService(
    private val messages: MessageSource
) {
    fun toListingUrl(listing: ListingModel): String? {
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

        val text = getMessage(
            "page.whatsapp.listing.text",
            arrayOf(listing.sellerAgentUser?.firstName ?: "", details)
        )
        return toUrl(listing.sellerAgentUser, text)
    }

    fun toAgentUrl(agent: AgentModel): String? {
        val text = getMessage("page.whatsapp.agent.text", arrayOf(agent.user.firstName))
        return toUrl(agent.user, text)
    }

    fun toNeighbourhoodUrl(neighbourhood: PlaceModel, agent: AgentModel): String? {
        val text = getMessage(
            "page.whatsapp.neighbourhood.text",
            arrayOf(agent.user.firstName, neighbourhood.name)
        )
        return toUrl(agent.user, text)
    }

    private fun toUrl(recipient: UserModel?, text: String): String? {
        return recipient?.mobile?.let { phone ->
            val xphone = phone.trimStart('+')
            return "https://wa.me/$xphone?text=" + URLEncoder.encode(text, "UTF-8")
        }
    }

    private fun getMessage(key: String, args: Array<Any>? = null, locale: Locale? = null): String {
        try {
            val loc = locale ?: LocaleContextHolder.getLocale()
            return messages.getMessage(key, args, loc)
        } catch (ex: Exception) {
            return key
        }
    }
}
