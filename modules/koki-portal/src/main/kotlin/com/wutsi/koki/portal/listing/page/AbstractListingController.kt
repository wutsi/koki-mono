package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingMetricDimension
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.portal.listing.form.ListingForm
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.module.page.AbstractModulePageController
import com.wutsi.koki.portal.refdata.model.LocationModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import java.text.SimpleDateFormat

abstract class AbstractListingController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "listing"

        fun loadPriceTrendMetrics(listing: ListingModel, model: Model, listingService: ListingService) {
            try {
                val neighbourhoodMetric = listing.address?.neighbourhood?.let { neighbourhood ->
                    model.addAttribute("priceTrendLocation", neighbourhood)
                    listingService.metrics(
                        listingType = listing.listingType,
                        propertyCategory = listing.propertyType?.category,
                        listingStatus = ListingStatus.ACTIVE,
                        neighbourhoodId = neighbourhood.id,
                        bedrooms = if (listing.propertyTypeResidential) listing.bedrooms else null,
                        dimension = ListingMetricDimension.NEIGHBORHOOD,
                    )
                }?.firstOrNull()
                if (neighbourhoodMetric != null) {
                    model.addAttribute("priceTrendMetric", neighbourhoodMetric)
                } else {
                    val cityMetric = listing.address?.city?.let { city ->
                        model.addAttribute("priceTrendLocation", city)
                        listingService.metrics(
                            listingType = listing.listingType,
                            propertyCategory = listing.propertyType?.category,
                            listingStatus = ListingStatus.ACTIVE,
                            cityId = city.id,
                            dimension = ListingMetricDimension.CITY,
                            bedrooms = listing.bedrooms,
                        )
                    }?.firstOrNull()
                    if (cityMetric != null) {
                        model.addAttribute("priceTrendMetric", cityMetric)
                    }
                }
            } catch (ex: Exception) {
                // Ignore
            }
        }
    }

    @Autowired
    protected lateinit var listingService: ListingService

    override fun getModuleName(): String {
        return MODULE_NAME
    }

    protected open fun findListing(id: Long): ListingModel {
        return listingService.get(id)
    }

    protected fun toListingForm(listing: ListingModel, city: LocationModel? = null): ListingForm {
        val df = SimpleDateFormat("yyyy-MM-dd")
        return ListingForm(
            id = listing.id,
            listingNumber = listing.listingNumber.toString(),
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
            roadPavement = listing.roadPavement,
            distanceFromMainRoad = listing.distanceFromMainRoad,
            availableAt = listing.availableAt?.let { date -> df.format(date) },

            furnitureType = listing.furnitureType,
            amenityIds = listing.amenities.map { amenity -> amenity.id },

            country = (listing.address?.country ?: city?.country ?: getTenant()?.country)?.uppercase(),
            cityId = listing.address?.city?.id ?: city?.id,
            neighbourhoodId = listing.address?.neighbourhood?.id,
            street = listing.address?.street,

            latitude = listing.geoLocation?.latitude,
            longitude = listing.geoLocation?.longitude,

            publicRemarks = listing.publicRemarks,
            agentRemarks = listing.agentRemarks,

            price = listing.price?.amount?.toLong(),
            visitFees = listing.visitFees?.amount?.toLong(),
            buyerAgentCommission = listing.buyerAgentCommission,
            sellerAgentCommission = listing.sellerAgentCommission,

            securityDeposit = listing.securityDeposit,
            advanceRent = listing.advanceRent,
            leaseTerm = listing.leaseTerm,
            noticePeriod = listing.noticePeriod,

            landTitle = listing.landTitle,
            technicalFile = listing.technicalFile,
            numberOfSigners = listing.numberOfSigners,
            mutationType = listing.mutationType,
            transactionWithNotary = listing.transactionWithNotary,
            subdivided = listing.subdivided,
            morcelable = listing.morcelable,

            sellerContactId = listing.sellerContact?.id,

            soldAt = listing.soldAt?.let { date -> df.format(date) },
            salePrice = (listing.salePrice ?: listing.price)?.amount?.toLong(),
        )
    }
}
