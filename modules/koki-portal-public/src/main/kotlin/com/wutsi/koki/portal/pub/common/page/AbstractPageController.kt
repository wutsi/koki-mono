package com.wutsi.koki.portal.pub.common.page

import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.pub.common.model.PageModel
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.tenant.model.TenantModel
import com.wutsi.koki.portal.pub.tenant.service.CurrentTenantHolder
import com.wutsi.koki.portal.pub.user.model.UserModel
import com.wutsi.koki.portal.pub.user.service.CurrentUserHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.client.HttpClientErrorException
import tools.jackson.databind.json.JsonMapper
import java.util.Locale

abstract class AbstractPageController {
    @Autowired
    protected lateinit var jsonMapper: JsonMapper

    @Value("\${koki.webapp.asset-url}")
    protected lateinit var assetUrl: String

    @Value("\${koki.webapp.base-url}")
    protected lateinit var baseUrl: String

    @Autowired
    protected lateinit var tenantHolder: CurrentTenantHolder

    @Autowired
    protected lateinit var userHolder: CurrentUserHolder

    @Autowired
    protected lateinit var messages: MessageSource

    @ModelAttribute("tenant")
    fun getTenant(): TenantModel {
        return tenantHolder.get()
    }

    @ModelAttribute("user")
    fun getUser(): UserModel? {
        return userHolder.get()
    }

    protected open fun createPageModel(
        name: String,
        title: String,
        description: String? = null,
        image: String? = null,
        url: String? = null,
        updatedTime: Long? = null,
    ): PageModel {
        return PageModel(
            name = name,
            title = title,
            description = description,
            image = image,
            url = url,
            assetUrl = assetUrl,
            language = LocaleContextHolder.getLocale().language,
            updatedTime = updatedTime,
        )
    }

    protected fun toErrorResponse(ex: HttpClientErrorException): ErrorResponse {
        return jsonMapper.readValue(ex.responseBodyAsString, ErrorResponse::class.java)
    }

    protected fun getMessage(key: String, args: Array<Any>? = null, locale: Locale? = null): String {
        try {
            val loc = locale ?: LocaleContextHolder.getLocale()
            return messages.getMessage(key, args, loc)
        } catch (ex: Exception) {
            return key
        }
    }

    protected fun toMapMarkersJson(listings: List<ListingModel>): String {
        val beds = getMessage("page.listing.bedrooms-abbreviation")
        val markers = listings
            .filter { listing -> listing.geoLocation != null }
            .map { listing ->
                mapOf(
                    "id" to listing.id,
                    "rental" to (listing.listingType == ListingType.RENTAL),
                    "sold" to (listing.status == ListingStatus.RENTED || listing.status == ListingStatus.SOLD),
                    "latitude" to listing.geoLocation?.latitude,
                    "longitude" to listing.geoLocation?.longitude,
                    "location" to listOfNotNull(listing.address?.neighbourhood?.name, listing.address?.city?.name)
                        .joinToString(", "),
                    "price" to if (listing.statusSold) listing.salePrice?.displayText else listing.price?.displayText,
                    "heroImageUrl" to listing.heroImageUrl,
                    "bedrooms" to (listing.bedrooms?.toString() ?: "--") + " " + beds,
                    "area" to ((listing.lotArea?.let { listing.propertyArea }?.toString() ?: "--") + "m2"),
                    "url" to listing.publicUrl,
                    "status" to if (listing.status == ListingStatus.RENTED) {
                        getMessage("listing-status.RENTED") + " " + listing.soldAtText
                    } else if (listing.status == ListingStatus.SOLD) {
                        getMessage("listing-status.SOLD") + " " + listing.soldAtText
                    } else {
                        ""
                    }
                )
            }
        return jsonMapper.writeValueAsString(markers)
    }
}
