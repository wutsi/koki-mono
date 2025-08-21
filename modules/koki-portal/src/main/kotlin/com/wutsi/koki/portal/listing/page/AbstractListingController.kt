package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.listing.form.ListingForm
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.module.page.AbstractModulePageController
import io.lettuce.core.KillArgs.Builder.id
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractListingController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "listing"
    }

    @Autowired
    protected lateinit var listingService: ListingService

    override fun getModuleName(): String {
        return MODULE_NAME
    }

    protected open fun findListing(id: Long): ListingModel {
        return listingService.get(id)
    }

    protected fun toListingForm(listing: ListingModel): ListingForm {
        return ListingForm(
            id = listing.id,
            listingNumber = listing.listingNumber,
            listingType = listing.listingType,
            propertyType = listing.propertyType,
            bedrooms = listing.bedrooms,
            bathrooms = listing.bathrooms,
            halfBathrooms = listing.halfBathrooms,
            floors = listing.floors,
            basementType = listing.basementType,
            level = listing.level,
            unit = listing.unit,
            parkings = listing.parkings,
            parkingType = listing.parkingType,
            fenceType = listing.fenceType,
            lotArea = listing.lotArea,
            propertyArea = listing.propertyArea,
            year = listing.year,

            furnitureType = listing.furnitureType,
            amenityIds = listing.amenities.map { amenity -> amenity.id },

            country = listing.address?.country,
            cityId = listing.address?.city?.id,
            neighbourhoodId = listing.address?.neighbourhood?.id,
            street = listing.address?.street,

            latitude = listing.geoLocation?.latitude,
            longitude = listing.geoLocation?.longitude,

            publicRemarks = listing.publicRemarks,
            agentRemarks = listing.agentRemarks,

            price = listing.price?.value?.toLong(),
            visitFees = listing.visitFees?.value?.toLong(),
            buyerAgentCommission = listing.buyerAgentCommission,
            sellerAgentCommission = listing.sellerAgentCommission,

            securityDeposit = listing.securityDeposit?.value?.toLong(),
            advanceRent = listing.advanceRent,
            leaseTerm = listing.leaseTerm,
            noticePeriod = listing.noticePeriod,

            sellerName = listing.sellerName,
            sellerPhone = listing.sellerPhone,
            sellerEmail = listing.sellerEmail,
            sellerIdType = listing.sellerIdType,
            sellerIdNumber = listing.sellerIdNumber,
            sellerIdCountry = listing.sellerIdCountry,
        )
    }
}
