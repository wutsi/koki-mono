package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingPublishRule
import jakarta.validation.ValidationException

class ListingMustNotHaveImageUnderReviewRule(private val fileService: FileService) : ListingPublishRule {
    override fun validate(listing: ListingEntity) {
        val images = fileService.search(
            tenantId = listing.tenantId,
            ownerId = listing.id,
            ownerType = ObjectType.LISTING,
            type = FileType.IMAGE,
            status = FileStatus.UNDER_REVIEW,
            limit = 1
        )
        if (images.isNotEmpty()) {
            throw ValidationException(ErrorCode.LISTING_IMAGE_UNDER_REVIEW)
        }
    }
}
