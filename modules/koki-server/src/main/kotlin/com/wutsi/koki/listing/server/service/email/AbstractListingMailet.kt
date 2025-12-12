package com.wutsi.koki.listing.server.service.email

import com.wutsi.koki.email.server.mq.AbstractMailet
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.springframework.context.MessageSource
import java.text.DecimalFormat
import java.util.Locale

abstract class AbstractListingMailet(
    private val locationService: LocationService,
    private val fileService: FileService,
    private val messages: MessageSource,
) : AbstractMailet() {
    protected fun getLocale(recipient: UserEntity): Locale {
        return recipient.language?.let { lang -> Locale.forLanguageTag(lang) } ?: Locale.FRENCH
    }

    protected fun getListingData(
        listing: ListingEntity,
        tenant: TenantEntity,
        recipient: UserEntity,
    ): Map<String, Any> {
        val locale = getLocale(recipient)
        val city = listing.cityId?.let { id -> locationService.get(id) }
        val neighbourhood = listing.neighbourhoodId?.let { id -> locationService.get(id) }
        val address = listOfNotNull(
            listing.street,
            neighbourhood?.name,
            city?.name
        ).joinToString(", ")
        val description = listOfNotNull(
            listing.bedrooms?.let { b -> "$b " + messages.getMessage("listing.bedrooms", arrayOf(), locale) },
            listing.bathrooms?.let { b -> "$b " + messages.getMessage("listing.bathrooms", arrayOf(), locale) },
            (listing.propertyArea ?: listing.lotArea)?.let { a -> "$a m2" }
        ).joinToString(" | ")
        val imageUrl = listing.heroImageId?.let { id -> fileService.get(id, listing.tenantId).url }

        val fmt = DecimalFormat(tenant.monetaryFormat)

        return mapOf(
            "listingNumber" to listing.listingNumber,
            "listingUrl" to "${tenant.portalUrl}/listings/${listing.id}",
            "listingImageUrl" to imageUrl,
            "listingDescription" to description,
            "listingAddress" to address,
            "listingPrice" to format(listing.price, fmt, listing.listingType),
            "listingSalePrice" to format(listing.salePrice, fmt, listing.listingType),
            "listingBuyerCommissionPercent" to (listing.buyerAgentCommission ?: 0).toString() + "%",
        ).filter { entry -> entry.value != null } as Map<String, Any>
    }

    private fun format(price: Long?, fmt: DecimalFormat, listingType: ListingType?): String? {
        return price?.let { fmt.format(price) + if (listingType == ListingType.RENTAL) "/mo" else "" }
    }
}
