package com.wutsi.koki.listing.server.service

import com.wutsi.koki.listing.server.domain.ListingEntity
import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class ListingPublisherValidator {
    @Throws(ValidationException::class)
    fun validate(listing: ListingEntity) {
    }
}
