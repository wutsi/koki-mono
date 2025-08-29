package com.wutsi.koki.listing.server.service

import com.wutsi.koki.listing.server.domain.ListingEntity
import jakarta.validation.ValidationException

interface ListingPublishRule {
    @Throws(ValidationException::class)
    fun validate(listing: ListingEntity)
}
