package com.wutsi.koki.portal.listing.service

import com.wutsi.koki.listing.dto.CloseListingRequest
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.Listing
import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.UpdateListingAddressRequest
import com.wutsi.koki.listing.dto.UpdateListingAmenitiesRequest
import com.wutsi.koki.listing.dto.UpdateListingGeoLocationRequest
import com.wutsi.koki.listing.dto.UpdateListingLeasingRequest
import com.wutsi.koki.listing.dto.UpdateListingPriceRequest
import com.wutsi.koki.listing.dto.UpdateListingRemarksRequest
import com.wutsi.koki.listing.dto.UpdateListingRequest
import com.wutsi.koki.listing.dto.UpdateListingSellerRequest
import com.wutsi.koki.portal.agent.service.AgentService
import com.wutsi.koki.portal.common.model.ResultSetModel
import com.wutsi.koki.portal.contact.service.ContactService
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
import java.text.SimpleDateFormat
import java.util.Collections.emptyMap

@Service
class ListingService(
    private val koki: KokiListings,
    private val mapper: ListingMapper,
    private val locationService: LocationService,
    private val userService: UserService,
    private val amenityService: AmenityService,
    private val fileService: FileService,
    private val contactService: ContactService,
    private val agentService: AgentService,
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

        val userIds = listOf(listing.createdById, listing.sellerAgentUserId, listing.buyerAgentUserId)
            .filterNotNull()
            .distinct()
        val users = if (!fullGraph || userIds.isEmpty()) {
            emptyMap<Long, UserModel>()
        } else {
            userService.search(
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
                listing.heroImageId!! to findHeroImage(listing)
            )
        }

        val contactIds = listOf(listing.sellerContactId, listing.buyerContactId).filterNotNull()
        val contacts = if (contactIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            contactService.search(
                ids = contactIds,
                limit = contactIds.size,
                fullGraph = false
            ).associateBy { contact -> contact.id }
        }

        val agentUserIds = listOf(listing.buyerAgentUserId, listing.sellerAgentUserId).filterNotNull()
        val agents = if (agentUserIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            agentService.search(
                userIds = agentUserIds,
                limit = agentUserIds.size
            ).associateBy { agent -> agent.id }
        }

        return mapper.toListingModel(
            entity = listing,
            locations = locations,
            users = users,
            amenities = amenities,
            images = images,
            contacts = contacts,
            agents = agents,
        )
    }

    fun search(
        ids: List<Long> = emptyList(),
        listingNumber: Long? = null,
        locationIds: List<Long> = emptyList(),
        listingType: ListingType? = null,
        propertyTypes: List<PropertyType> = emptyList(),
        furnitureTypes: List<FurnitureType> = emptyList(),
        statuses: List<ListingStatus> = emptyList(),
        bedrooms: String? = null,
        bathrooms: String? = null,
        minPrice: Long? = null,
        maxPrice: Long? = null,
        minLotArea: Int? = null,
        maxLotArea: Int? = null,
        minPropertyArea: Int? = null,
        maxPropertyArea: Int? = null,
        sellerAgentUserId: Long? = null,
        buyerAgentUserId: Long? = null,
        agentUserId: Long? = null,
        sortBy: ListingSort? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): ResultSetModel<ListingModel> {
        val response = koki.search(
            ids = ids,
            listingNumber = listingNumber,
            locationIds = locationIds,
            listingType = listingType,
            propertyTypes = propertyTypes,
            furnitureTypes = furnitureTypes,
            statuses = statuses,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minLotArea = minLotArea,
            maxLotArea = maxLotArea,
            minPropertyArea = minPropertyArea,
            maxPropertyArea = maxPropertyArea,
            sellerAgentUserId = sellerAgentUserId,
            buyerAgentUserId = buyerAgentUserId,
            agentUserId = agentUserId,
            sortBy = sortBy,
            limit = limit,
            offset = offset,
        )
        val listings = response.listings
        val locationIds = listings.flatMap { listing ->
            listOf(listing.address?.cityId, listing.address?.stateId, listing.address?.neighborhoodId)
        }
            .filterNotNull()
            .distinct()
        val locations = if (!fullGraph || locationIds.isEmpty()) {
            emptyMap<Long, LocationModel>()
        } else {
            locationService.search(
                ids = locationIds, limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        val userIds = listings.flatMap { listing ->
            listOf(listing.sellerAgentUserId, listing.buyerAgentUserId)
        }
            .filterNotNull()
            .distinct()
        val users = if (!fullGraph || userIds.isEmpty()) {
            emptyMap<Long, UserModel>()
        } else {
            userService.search(
                ids = userIds, limit = userIds.size
            ).associateBy { user -> user.id }
        }

        val imageIds = listings.mapNotNull { listing -> listing.heroImageId }
        val images = if (!fullGraph || imageIds.isEmpty()) {
            emptyMap<Long, FileModel>()
        } else {
            fileService.search(
                ids = imageIds,
                limit = imageIds.size
            ).associateBy { image -> image.id }
        }

        return ResultSetModel<ListingModel>(
            total = response.total,
            items = response.listings.map { listing ->
                mapper.toListingModel(
                    entity = listing,
                    locations = locations,
                    users = users,
                    images = images
                )
            }
        )
    }

    private fun findHeroImage(listing: Listing): FileModel? {
        return try {
            listing.heroImageId?.let { id -> fileService.get(id) }
        } catch (ex: Exception) {
            null
        }
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
        val df = SimpleDateFormat("yyyy-MM-dd")
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
                roadPavement = form.roadPavement,
                distanceFromMainRoad = form.distanceFromMainRoad,
                availableAt = form.availableAt?.ifEmpty { null }?.let { date -> df.parse(date) }
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
                sellerContactId = form.sellerContactId,
            )
        )
    }

    fun publish(id: Long) {
        koki.publish(id)
    }

    fun close(form: ListingForm) {
        koki.close(
            form.id,
            CloseListingRequest(
                status = form.status,
                comment = form.comment,
            )
        )
    }
}
