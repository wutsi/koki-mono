package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingPublishRule
import jakarta.validation.ValidationException

class ListingMustHaveGeneralInformationRule : ListingPublishRule {
    override fun validate(listing: ListingEntity) {
        if (listing.propertyType == PropertyType.LAND ||
            listing.propertyType == PropertyType.COMMERCIAL ||
            listing.propertyType == PropertyType.INDUSTRIAL
        ) {
            validateLand(listing)
        } else if (
            listing.propertyType == PropertyType.APARTMENT ||
            listing.propertyType == PropertyType.STUDIO ||
            listing.propertyType == PropertyType.DUPLEX ||
            listing.propertyType == PropertyType.HOUSE
        ) {
            validateHouse(listing)
        }
    }

    fun validateHouse(listing: ListingEntity) {
        if (undefined(listing.bedrooms) || undefined(listing.bathrooms)) {
            throw ValidationException(ErrorCode.LISTING_MISSING_GENERAL_INFORMATION_HOUSE)
        }
    }

    fun validateLand(listing: ListingEntity) {
        if (undefined(listing.lotArea)) {
            throw ValidationException(ErrorCode.LISTING_MISSING_GENERAL_INFORMATION_LAND)
        }
    }

    private fun undefined(value: Int?): Boolean {
        return value == null || value == 0
    }
}
