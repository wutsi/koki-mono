package com.wutsi.koki.listing.server.service

import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.validation.ListingMustHaveAddressRule
import com.wutsi.koki.listing.server.service.validation.ListingMustHaveGeneralInformationRule
import com.wutsi.koki.listing.server.service.validation.ListingMustHaveGeolocationRule
import com.wutsi.koki.listing.server.service.validation.ListingMustHaveImageApprovedRule
import com.wutsi.koki.listing.server.service.validation.ListingMustHavePriceRule
import com.wutsi.koki.listing.server.service.validation.ListingMustNotHaveImageUnderReviewRule
import jakarta.validation.ValidationException
import org.springframework.stereotype.Service

@Service
class ListingPublisherValidator(
    private val fileService: FileService
) {
    val rules = listOf(
        ListingMustHaveGeneralInformationRule(),
        ListingMustHaveAddressRule(),
        ListingMustHaveGeolocationRule(),
        ListingMustHavePriceRule(),
//        ListingMustHaveSellerAgentCommissionRule(),
//        ListingMustHaveValidBuyerAgentCommissionRule(),
//        ListingMustHaveSellerRule(),
        ListingMustHaveImageApprovedRule(fileService),
        ListingMustNotHaveImageUnderReviewRule(fileService)
    )

    @Throws(ValidationException::class)
    fun validate(listing: ListingEntity) {
        rules.forEach { rule -> rule.validate(listing) }
    }
}
