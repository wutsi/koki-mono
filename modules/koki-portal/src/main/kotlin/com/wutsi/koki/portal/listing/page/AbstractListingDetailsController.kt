package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractListingDetailsController : AbstractModuleDetailsPageController() {
    @Autowired
    protected lateinit var listingService: ListingService

    override fun getModuleName(): String {
        return AbstractListingController.MODULE_NAME
    }

    protected fun findListing(id: Long): ListingModel {
        val listing = listingService.get(id)
        if (!listing.canAccess(getUser())) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(403))
        }
        return listing
    }
}
