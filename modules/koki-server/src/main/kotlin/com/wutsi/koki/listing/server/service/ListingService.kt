package com.wutsi.koki.listing.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.UpdateListingAddressRequest
import com.wutsi.koki.listing.dto.UpdateListingAmenitiesRequest
import com.wutsi.koki.listing.dto.UpdateListingGeoLocationRequest
import com.wutsi.koki.listing.dto.UpdateListingLeasingRequest
import com.wutsi.koki.listing.dto.UpdateListingPriceRequest
import com.wutsi.koki.listing.dto.UpdateListingRemarksRequest
import com.wutsi.koki.listing.dto.UpdateListingRequest
import com.wutsi.koki.listing.dto.UpdateListingSellerRequest
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.listing.server.dao.ListingSequenceRepository
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.domain.ListingSequenceEntity
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Date

@Service
class ListingService(
    private val dao: ListingRepository,
    private val sequenceDao: ListingSequenceRepository,
    private val securityService: SecurityService,
    private val amenityService: AmenityService,
    private val locationService: LocationService,
    private val configurationService: ConfigurationService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListingService::class.java)
    }

    fun get(id: Long, tenantId: Long): ListingEntity {
        val room = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.LISTING_NOT_FOUND)) }

        if (room.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.LISTING_NOT_FOUND))
        }
        return room
    }

    @Transactional
    fun create(request: CreateListingRequest, tenantId: Long): ListingEntity {
        val now = Date()
        val userId = securityService.getCurrentUserIdOrNull()
        return dao.save(
            ListingEntity(
                tenantId = tenantId,
                listingNumber = generateListingNumber(tenantId),
                status = ListingStatus.DRAFT,
                listingType = request.listingType,
                propertyType = request.propertyType,
                bedrooms = request.bedrooms,
                bathrooms = request.bedrooms,
                halfBathrooms = request.halfBathrooms,
                floors = request.floors,
                basementType = request.basementType,
                level = request.level,
                unit = request.unit,
                parkings = request.parkings,
                parkingType = request.parkingType,
                fenceType = request.fenceType,
                lotArea = request.lotArea,
                propertyArea = request.propertyArea,
                year = request.year,
                sellerAgentUserId = userId,
                createdAt = now,
                modifiedAt = now,
                createdById = userId,
                modifiedById = userId,
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdateListingRequest, tenantId: Long) {
        val listing = get(id, tenantId)

        listing.listingType = request.listingType
        listing.propertyType = request.propertyType
        listing.bedrooms = request.bedrooms
        listing.bathrooms = request.bedrooms
        listing.halfBathrooms = request.halfBathrooms
        listing.floors = request.floors
        listing.basementType = request.basementType
        listing.level = request.level
        listing.unit = request.unit
        listing.parkings = request.parkings
        listing.parkingType = request.parkingType
        listing.fenceType = request.fenceType
        listing.lotArea = request.lotArea
        listing.propertyArea = request.propertyArea
        listing.year = request.year
        save(listing)
    }

    @Transactional
    fun amenities(id: Long, request: UpdateListingAmenitiesRequest, tenantId: Long) {
        val listing = get(id, tenantId)

        listing.furnitureType = request.furnitureType
        listing.amenities = if (request.amenityIds.isEmpty()) {
            mutableListOf<AmenityEntity>()
        } else {
            amenityService.search(
                ids = request.amenityIds,
                limit = request.amenityIds.size,
            ).toMutableList()
        }
        save(listing)
    }

    @Transactional
    fun address(id: Long, request: UpdateListingAddressRequest, tenantId: Long) {
        val listing = get(id, tenantId)
        val city = request.address?.cityId?.let { id -> locationService.get(id) }

        listing.cityId = request.address?.cityId
        listing.neighbourhoodId = request.address?.neighborhoodId
        listing.stateId = city?.parentId
        listing.country = request.address?.country?.lowercase()?.ifEmpty { null }
        listing.street = request.address?.street?.ifEmpty { null }
        listing.postalCode = request.address?.postalCode?.uppercase()?.ifEmpty { null }
        save(listing)
    }

    @Transactional
    fun geoLocation(id: Long, request: UpdateListingGeoLocationRequest, tenantId: Long) {
        val listing = get(id, tenantId)

        listing.latitude = request.geoLocation?.latitude
        listing.longitude = request.geoLocation?.longitude
        save(listing)
    }

    @Transactional
    fun price(id: Long, request: UpdateListingPriceRequest, tenantId: Long) {
        val listing = get(id, tenantId)

        listing.price = request.price
        listing.visitFees = request.visitFees
        listing.currency = request.currency?.uppercase()?.ifEmpty { null }
        listing.sellerAgentCommission = request.sellerAgentCommission
        listing.buyerAgentCommission = request.buyerAgentCommission
        save(listing)
    }

    @Transactional
    fun leasing(id: Long, request: UpdateListingLeasingRequest, tenantId: Long) {
        val listing = get(id, tenantId)

        listing.leaseTerm = request.leaseTerm
        listing.noticePeriod = request.noticePeriod
        listing.securityDeposit = request.securityDeposit
        listing.advanceRent = request.advanceRent
        save(listing)
    }

    @Transactional
    fun seller(id: Long, request: UpdateListingSellerRequest, tenantId: Long) {
        val listing = get(id, tenantId)

        listing.sellerName = request.sellerName?.uppercase()?.ifEmpty { null }
        listing.sellerEmail = request.sellerEmail?.lowercase()?.ifEmpty { null }
        listing.sellerPhone = request.sellerPhone?.ifEmpty { null }
        listing.sellerIdNumber = request.sellerIdNumber
        listing.sellerIdType = request.sellerIdType
        listing.sellerIdCountry = request.sellerIdCountry?.lowercase()?.ifEmpty { null }
        save(listing)
    }

    @Transactional
    fun remarks(id: Long, request: UpdateListingRemarksRequest, tenantId: Long) {
        val listing = get(id, tenantId)

        listing.publicRemarks = request.publicRemarks?.ifEmpty { null }
        listing.agentRemarks = request.agentRemarks?.ifEmpty { null }
        save(listing)
    }

    @Transactional
    fun save(listing: ListingEntity, modifyById: Long? = null) {
        listing.modifiedAt = Date()
        listing.modifiedById = modifyById ?: securityService.getCurrentUserIdOrNull()
        dao.save(listing)
    }

    private fun generateListingNumber(tenantId: Long): Long {
        // Generate number
        var seq = sequenceDao.findByTenantId(tenantId)
        if (seq == null) {
            seq = sequenceDao.save(
                ListingSequenceEntity(
                    tenantId = tenantId,
                    current = 1,
                )
            )
        } else {
            seq.current = seq.current + 1
            sequenceDao.save(seq)
        }

        // Start
        val configs = configurationService.search(
            tenantId = tenantId,
            names = listOf(ConfigurationName.LISTING_START_NUMBER)
        )
        val start = if (configs.isEmpty()) {
            0L
        } else {
            try {
                configs[0].value.toLong()
            } catch (ex: Exception) {
                LOGGER.warn("Invalid configuration. ${configs[0].name}=${configs[0].value}", ex)
                0L
            }
        }
        return start + seq.current
    }
}
