package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.portal.listing.model.ListingModel
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractEditorListingController : AbstractEditListingController() {
    override fun findListing(id: Long): ListingModel {
        val listing = super.findListing(id)
        if (listing.status != ListingStatus.DRAFT) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(403))
        }

        return listing
    }
}
