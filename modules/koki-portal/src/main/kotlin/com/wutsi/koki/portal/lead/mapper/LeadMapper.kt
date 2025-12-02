package com.wutsi.koki.portal.lead.mapper

import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadSummary
import com.wutsi.koki.portal.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.lead.model.LeadModel
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class LeadMapper : TenantAwareMapper() {
    fun toLeadModel(entity: Lead, listing: ListingModel, city: LocationModel?): LeadModel {
        val df = createDateFormat()
        return LeadModel(
            id = entity.id,
            listing = listing,
            firstName = entity.firstName,
            lastName = entity.lastName,
            displayName = "${entity.firstName} ${entity.lastName}".trim(),
            email = entity.email,
            phoneNumber = entity.phoneNumber,
            phoneNumberFormatted = entity.phoneNumber.ifEmpty { null }?.let { number -> formatPhoneNumber(number) },
            visitRequestedAt = entity.visitRequestedAt,
            status = entity.status,
            source = entity.source,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = df.format(entity.modifiedAt),
            nextContactAt = entity.nextContactAt,
            nextContactAtText = entity.nextContactAt?.let { date -> df.format(date) },
            nextVisitAt = entity.nextVisitAt,
            nextVisitAtText = entity.nextVisitAt?.let { date -> df.format(date) },
            createdAt = entity.createdAt,
            createdAtText = df.format(entity.createdAt),
            message = entity.message,
            address = toAddress(city, entity.country),
        )
    }

    fun toLeadModel(
        entity: LeadSummary,
        listings: Map<Long, ListingModel>,
        cities: Map<Long, LocationModel>
    ): LeadModel {
        val df = createDateFormat()
        return LeadModel(
            id = entity.id,
            listing = listings[entity.listingId] ?: ListingModel(id = entity.listingId),
            firstName = entity.firstName,
            lastName = entity.lastName,
            displayName = "${entity.firstName} ${entity.lastName}".trim(),
            email = entity.email,
            phoneNumber = entity.phoneNumber,
            phoneNumberFormatted = entity.phoneNumber.ifEmpty { null }?.let { number -> formatPhoneNumber(number) },
            visitRequestedAt = entity.visitRequestedAt,
            status = entity.status,
            source = entity.source,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = df.format(entity.modifiedAt),
            nextContactAt = entity.nextContactAt,
            nextContactAtText = entity.nextContactAt?.let { date -> df.format(date) },
            nextVisitAt = entity.nextVisitAt,
            nextVisitAtText = entity.nextVisitAt?.let { date -> df.format(date) },
            createdAt = entity.createdAt,
            createdAtText = df.format(entity.createdAt),
            address = toAddress(
                city = entity.cityId?.let { id -> cities[id] },
                country = entity.country
            )
        )
    }

    private fun toAddress(city: LocationModel?, country: String?): AddressModel? {
        if (city == null && country == null) {
            return null
        }
        val xcountry = city?.country ?: country
        return AddressModel(
            country = xcountry,
            countryName = Locale(LocaleContextHolder.getLocale().language, xcountry).displayCountry,
            city = city,
        )
    }
}
