package com.wutsi.koki.portal.listing.service

import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.dto.UpdateListingAddressRequest
import com.wutsi.koki.listing.dto.UpdateListingAmenitiesRequest
import com.wutsi.koki.listing.dto.UpdateListingGeoLocationRequest
import com.wutsi.koki.listing.dto.UpdateListingLeasingRequest
import com.wutsi.koki.listing.dto.UpdateListingPriceRequest
import com.wutsi.koki.listing.dto.UpdateListingRemarksRequest
import com.wutsi.koki.listing.dto.UpdateListingRequest
import com.wutsi.koki.listing.dto.UpdateListingSellerRequest
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.listing.form.ListingForm
import com.wutsi.koki.portal.listing.mapper.ListingMapper
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.refdata.service.AmenityService
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.GeoLocation
import com.wutsi.koki.sdk.KokiListings
import org.springframework.stereotype.Service

@Service
class ListingService(
    private val koki: KokiListings,
    private val mapper: ListingMapper,
    private val locationService: LocationService,
    private val userService: UserService,
    private val amenityService: AmenityService,
    private val fileService: FileService,
) {
    fun get(id: Long, fullGraph: Boolean = true): ListingModel {
        val listing = koki.get(id).listing

        val locationIds = listOf(listing.address?.cityId, listing.address?.stateId, listing.address?.neighborhoodId)
            .filterNotNull()
            .distinct()
        val locations = if (!fullGraph || locationIds.isEmpty()) {
            emptyMap<Long, LocationModel>()
        } else {
            locationService.search(
                ids = locationIds, limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        val userIds = listOf(listing.createdById, listing.sellerAgentUserId)
            .filterNotNull()
            .distinct()
        val users = if (!fullGraph || userIds.isEmpty()) {
            emptyMap<Long, UserModel>()
        } else {
            userService.users(
                ids = userIds, limit = userIds.size
            ).associateBy { user -> user.id }
        }

        val amenities = if (!fullGraph || listing.amenityIds.isEmpty()) {
            emptyMap<Long, AmenityModel>()
        } else {
            amenityService.amenities(
                ids = listing.amenityIds, limit = listing.amenityIds.size
            ).associateBy { amenity -> amenity.id }
        }

        val images = if (!fullGraph || listing.heroImageId == null) {
            emptyMap<Long, FileModel>()
        } else {
            mapOf(
                listing.heroImageId!! to fileService.get(listing.heroImageId!!)
            )
        }

        return mapper.toListingModel(
            entity = listing,
            locations = locations,
            users = users,
            amenities = amenities,
            images = images
        )
    }

    fun create(form: ListingForm): Long {
        return koki.create(
            CreateListingRequest(
                listingType = form.listingType,
                propertyType = form.propertyType,
            )
        ).listingId
    }

    fun update(form: ListingForm) {
        koki.update(
            form.id,
            UpdateListingRequest(
                listingType = form.listingType,
                propertyType = form.propertyType,
                fenceType = form.fenceType,
                unit = form.unit,
                level = form.level,
                year = form.year,
                propertyArea = form.propertyArea,
                lotArea = form.lotArea,
                parkingType = form.parkingType,
                parkings = form.parkings,
                basementType = form.basementType,
                floors = form.floors,
                halfBathrooms = form.halfBathrooms,
                bedrooms = form.bedrooms,
                bathrooms = form.bathrooms,
            )
        )
    }

    fun updateAmenities(form: ListingForm) {
        koki.updateAmenities(
            form.id,
            UpdateListingAmenitiesRequest(
                furnitureType = form.furnitureType,
                amenityIds = form.amenityIds,
            )
        )
    }

    fun updateAddress(form: ListingForm) {
        koki.updateAddress(
            form.id,
            UpdateListingAddressRequest(
                address = Address(
                    country = form.country,
                    cityId = form.cityId,
                    neighborhoodId = form.neighbourhoodId,
                    street = form.street,
                )
            )
        )
    }

    fun updateGeoLocation(form: ListingForm) {
        koki.updateGeoLocation(
            form.id,
            UpdateListingGeoLocationRequest(
                geoLocation = if (form.latitude == null || form.longitude == null) {
                    null
                } else {
                    GeoLocation(
                        latitude = form.latitude,
                        longitude = form.longitude
                    )
                }
            )
        )
    }

    fun updateRemarks(form: ListingForm) {
        koki.updateRemarks(
            form.id,
            UpdateListingRemarksRequest(
                publicRemarks = form.publicRemarks,
                agentRemarks = form.agentRemarks,
            )
        )
    }

    fun updatePrice(form: ListingForm) {
        koki.updatePrice(
            form.id,
            UpdateListingPriceRequest(
                price = form.price,
                visitFees = form.visitFees,
                currency = form.currency,
                buyerAgentCommission = form.buyerAgentCommission,
                sellerAgentCommission = form.sellerAgentCommission,
            )
        )
    }

    fun updateLeasing(form: ListingForm) {
        koki.updateLeasing(
            form.id,
            UpdateListingLeasingRequest(
                advanceRent = form.advanceRent,
                securityDeposit = form.securityDeposit,
                leaseTerm = form.leaseTerm,
                noticePeriod = form.noticePeriod,
            )
        )
    }

    fun updateSeller(form: ListingForm) {
        koki.updateSeller(
            form.id,
            UpdateListingSellerRequest(
                sellerName = form.sellerName,
                sellerEmail = form.sellerEmail,
                sellerPhone = form.sellerPhoneFull,
                sellerIdCountry = form.sellerIdCountry,
                sellerIdNumber = form.sellerIdNumber,
                sellerIdType = form.sellerIdType,
            )
        )
    }

    fun publish(id: Long) {
        koki.publish(id)
    }
}
