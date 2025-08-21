package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.listing.model.ListingModel
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractEditListingController : AbstractListingController() {
    override fun findListing(id: Long): ListingModel {
        val listing = super.findListing(id)
        if (!listing.canManage(getUser())) { // Not allowed to edit another agent listing
            throw HttpClientErrorException(HttpStatusCode.valueOf(403))
        }

        return listing
    }
}
