package com.wutsi.koki.listing.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.listing.dto.CloseListingRequest
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.dto.FurnitureType
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
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.listing.server.dao.ListingSequenceRepository
import com.wutsi.koki.listing.server.dao.ListingStatusRepository
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.domain.ListingSequenceEntity
import com.wutsi.koki.listing.server.domain.ListingStatusEntity
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Date

@Service
class ListingService(
    private val dao: ListingRepository,
    private val sequenceDao: ListingSequenceRepository,
    private val statusDao: ListingStatusRepository,
    private val securityService: SecurityService,
    private val amenityService: AmenityService,
    private val locationService: LocationService,
    private val configurationService: ConfigurationService,
    private val publisherValidator: ListingPublisherValidator,
    private val em: EntityManager,
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

    fun search(
        tenantId: Long,
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
    ): List<ListingEntity> {
        val jql = StringBuilder("SELECT L FROM ListingEntity L WHERE L.tenantId = :tenantId")

        // WHERE
        if (ids.isNotEmpty()) {
            jql.append(" AND L.id IN :ids")
        }
        if (listingNumber != null) {
            jql.append(" AND L.listingNumber = :listingNumber")
        }
        if (locationIds.isNotEmpty()) {
            jql.append(" AND (L.cityId IN :locationIds OR L.neighbourhoodId IN :locationIds)")
        }
        if (listingType != null) {
            jql.append(" AND L.listingType = :listingType")
        }
        if (propertyTypes.isNotEmpty()) {
            jql.append(" AND L.propertyType IN :propertyTypes")
        }
        if (furnitureTypes.isNotEmpty()) {
            jql.append(" AND L.furnitureType IN :furnitureTypes")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND L.status IN :statuses")
        }
        if (bedrooms != null) {
            if (bedrooms.endsWith("+")) {
                jql.append(" AND L.bedrooms >= :bedrooms")
            } else {
                jql.append(" AND L.bedrooms = :bedrooms")
            }
        }
        if (bathrooms != null) {
            if (bathrooms.endsWith("+")) {
                jql.append(" AND L.bathrooms >= :bathrooms")
            } else {
                jql.append(" AND L.bathrooms = :bathrooms")
            }
        }
        if (minPrice != null) {
            jql.append(" AND L.price >= :minPrice")
        }
        if (maxPrice != null) {
            jql.append(" AND L.price <= :maxPrice")
        }
        if (minLotArea != null) {
            jql.append(" AND L.lotArea >= :minLotArea")
        }
        if (maxLotArea != null) {
            jql.append(" AND L.lotArea <= :maxLotArea")
        }
        if (minPropertyArea != null) {
            jql.append(" AND L.propertyArea >= :minPropertyArea")
        }
        if (maxPropertyArea != null) {
            jql.append(" AND L.propertyArea <= :maxPropertyArea")
        }
        if (sellerAgentUserId != null) {
            jql.append(" AND L.sellerAgentUserId = :sellerAgentUserId")
        }
        if (buyerAgentUserId != null) {
            jql.append(" AND L.buyerAgentUserId = :buyerAgentUserId")
        }
        if (agentUserId != null) {
            jql.append(" AND (L.sellerAgentUserId = :agentUserId OR L.buyerAgentUserId = :agentUserId)")
        }

        // ORDER BY
        when (sortBy) {
            ListingSort.NEWEST -> "ORDER BY L.publishedAt DESC, L.createdAt DESC"
            ListingSort.OLDEST -> "ORDER BY L.publishedAt ASC, L.createdAt ASC"
            ListingSort.PRICE_HIGH_LOW -> "ORDER BY L.price DESC"
            ListingSort.PRICE_LOW_HIGH -> "ORDER BY L.price ASC"
            ListingSort.TRANSACTION_DATE -> "ORDER BY L.transactionDate DESC"
            ListingSort.MODIFIED_DATE -> "ORDER BY L.modifiedAt DESC"
            else -> "ORDER BY L.price ASC"
        }

        // PARAMETERS
        val query = em.createQuery(jql.toString(), ListingEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (listingNumber != null) {
            query.setParameter("listingNumber", listingNumber)
        }
        if (locationIds.isNotEmpty()) {
            query.setParameter("locationIds", locationIds)
        }
        if (listingType != null) {
            query.setParameter("listingType", listingType)
        }
        if (propertyTypes.isNotEmpty()) {
            query.setParameter("propertyTypes", propertyTypes)
        }
        if (furnitureTypes.isNotEmpty()) {
            query.setParameter("furnitureTypes", furnitureTypes)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }
        if (bedrooms != null) {
            query.setParameter("bedrooms", roomValue(bedrooms))
        }
        if (bathrooms != null) {
            query.setParameter("bathrooms", roomValue(bathrooms))
        }
        if (minPrice != null) {
            query.setParameter("minPrice", minPrice)
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice)
        }
        if (minLotArea != null) {
            query.setParameter("minLotArea", minLotArea)
        }
        if (maxLotArea != null) {
            query.setParameter("maxLotArea", maxLotArea)
        }
        if (minPropertyArea != null) {
            query.setParameter("minPropertyArea", minPropertyArea)
        }
        if (maxPropertyArea != null) {
            query.setParameter("maxPropertyArea", maxPropertyArea)
        }
        if (sellerAgentUserId != null) {
            query.setParameter("sellerAgentUserId", sellerAgentUserId)
        }
        if (buyerAgentUserId != null) {
            query.setParameter("buyerAgentUserId", buyerAgentUserId)
        }
        if (agentUserId != null) {
            query.setParameter("agentUserId", agentUserId)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    fun count(
        tenantId: Long,
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
    ): Long {
        val jql = StringBuilder("SELECT COUNT(*) FROM ListingEntity L WHERE L.tenantId = :tenantId")

        // WHERE
        if (ids.isNotEmpty()) {
            jql.append(" AND L.id IN :ids")
        }
        if (listingNumber != null) {
            jql.append(" AND L.listingNumber = :listingNumber")
        }
        if (locationIds.isNotEmpty()) {
            jql.append(" AND (L.cityId IN :locationIds OR L.neighbourhoodId IN :locationIds)")
        }
        if (listingType != null) {
            jql.append(" AND L.listingType = :listingType")
        }
        if (propertyTypes.isNotEmpty()) {
            jql.append(" AND L.propertyType IN :propertyTypes")
        }
        if (furnitureTypes.isNotEmpty()) {
            jql.append(" AND L.furnitureType IN :furnitureTypes")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND L.status IN :statuses")
        }
        if (bedrooms != null) {
            if (bedrooms.endsWith("+")) {
                jql.append(" AND L.bedrooms >= :bedrooms")
            } else {
                jql.append(" AND L.bedrooms = :bedrooms")
            }
        }
        if (bathrooms != null) {
            if (bathrooms.endsWith("+")) {
                jql.append(" AND L.bathrooms >= :bathrooms")
            } else {
                jql.append(" AND L.bathrooms = :bathrooms")
            }
        }
        if (minPrice != null) {
            jql.append(" AND L.price >= :minPrice")
        }
        if (maxPrice != null) {
            jql.append(" AND L.price <= :maxPrice")
        }
        if (minLotArea != null) {
            jql.append(" AND L.lotArea >= :minLotArea")
        }
        if (maxLotArea != null) {
            jql.append(" AND L.lotArea <= :maxLotArea")
        }
        if (minPropertyArea != null) {
            jql.append(" AND L.propertyArea >= :minPropertyArea")
        }
        if (maxPropertyArea != null) {
            jql.append(" AND L.propertyArea <= :maxPropertyArea")
        }
        if (sellerAgentUserId != null) {
            jql.append(" AND L.sellerAgentUserId = :sellerAgentUserId")
        }
        if (buyerAgentUserId != null) {
            jql.append(" AND L.buyerAgentUserId = :buyerAgentUserId")
        }
        if (agentUserId != null) {
            jql.append(" AND (L.sellerAgentUserId = :agentUserId OR L.buyerAgentUserId = :agentUserId)")
        }

        // PARAMETERS
        val query = em.createQuery(jql.toString())
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (listingNumber != null) {
            query.setParameter("listingNumber", listingNumber)
        }
        if (locationIds.isNotEmpty()) {
            query.setParameter("locationIds", locationIds)
        }
        if (listingType != null) {
            query.setParameter("listingType", listingType)
        }
        if (propertyTypes.isNotEmpty()) {
            query.setParameter("propertyTypes", propertyTypes)
        }
        if (furnitureTypes.isNotEmpty()) {
            query.setParameter("furnitureTypes", furnitureTypes)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }
        if (bedrooms != null) {
            query.setParameter("bedrooms", roomValue(bedrooms))
        }
        if (bathrooms != null) {
            query.setParameter("bathrooms", roomValue(bathrooms))
        }
        if (minPrice != null) {
            query.setParameter("minPrice", minPrice)
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice)
        }
        if (minLotArea != null) {
            query.setParameter("minLotArea", minLotArea)
        }
        if (maxLotArea != null) {
            query.setParameter("maxLotArea", maxLotArea)
        }
        if (minPropertyArea != null) {
            query.setParameter("minPropertyArea", minPropertyArea)
        }
        if (maxPropertyArea != null) {
            query.setParameter("maxPropertyArea", maxPropertyArea)
        }
        if (sellerAgentUserId != null) {
            query.setParameter("sellerAgentUserId", sellerAgentUserId)
        }
        if (buyerAgentUserId != null) {
            query.setParameter("buyerAgentUserId", buyerAgentUserId)
        }
        if (agentUserId != null) {
            query.setParameter("agentUserId", agentUserId)
        }

        return query.singleResult as Long
    }

    private fun roomValue(str: String): Int {
        return if (str.endsWith("+")) {
            str.substring(0, str.length - 1).toInt()
        } else {
            str.toInt()
        }
    }

    @Transactional
    fun create(request: CreateListingRequest, tenantId: Long): ListingEntity {
        val now = Date()
        val userId = securityService.getCurrentUserIdOrNull()
        val listing = dao.save(
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
                unit = request.unit?.uppercase()?.ifEmpty { null },
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
        statusDao.save(
            ListingStatusEntity(
                listing = listing,
                status = listing.status,
                createdAt = now,
                createdById = listing.createdById
            )
        )

        return listing
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
        listing.unit = request.unit?.uppercase()?.ifEmpty { null }
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
    fun save(listing: ListingEntity, modifyById: Long? = null): ListingEntity {
        listing.modifiedAt = Date()
        listing.modifiedById = modifyById ?: securityService.getCurrentUserIdOrNull()
        return dao.save(listing)
    }

    @Transactional
    fun publish(id: Long, tenantId: Long): ListingEntity {
        val listing = get(id, tenantId)
        if (listing.status != ListingStatus.DRAFT) {
            throwInvalidStatus("Cannot publish a listing in status: ${listing.status}")
        }

        try {
            // Update the listing
            publisherValidator.validate(listing)
            listing.status = ListingStatus.PUBLISHING
            listing.buyerAgentCommissionAmount = computeCommission(listing.price, listing.buyerAgentCommission)
            listing.sellerAgentCommissionAmount = computeCommission(listing.price, listing.sellerAgentCommission)
            save(listing)

            // Record the status
            statusDao.save(
                ListingStatusEntity(
                    listing = listing,
                    status = listing.status,
                    createdAt = Date(),
                    createdById = securityService.getCurrentUserIdOrNull(),
                )
            )

            return listing
        } catch (ex: ValidationException) {
            throw ConflictException(
                Error(
                    code = ex.message ?: ErrorCode.LISTING_FAILED_VALIDATION
                )
            )
        }
    }

    @Transactional
    fun close(id: Long, request: CloseListingRequest, tenantId: Long): ListingEntity {
        val listing = get(id, tenantId)
        if (listing.status != ListingStatus.ACTIVE) {
            throwInvalidStatus("Cannot close a listing in status: ${listing.status}")
        }

        when (request.status) {
            ListingStatus.RENTED -> {
                if (listing.listingType != ListingType.RENTAL) {
                    throwInvalidStatus("The listing is not a RENTAL. It's status cannot be RENTED")
                }
            }

            ListingStatus.SOLD -> {
                if (listing.listingType != ListingType.SALE) {
                    throwInvalidStatus("The listing is not a SALE. It's status cannot be SOLD")
                }
            }

            ListingStatus.WITHDRAWN, ListingStatus.EXPIRED, ListingStatus.CANCELLED -> {}

            else -> throwInvalidStatus("Invalid status")
        }

        // Update the listing
        val now = Date()
        listing.status = request.status
        listing.closedAt = now
        if (request.status == ListingStatus.RENTED || request.status == ListingStatus.SOLD) {
            listing.buyerName = request.buyerName?.ifEmpty { null }
            listing.buyerEmail = request.buyerEmail?.lowercase()?.ifEmpty { null }
            listing.buyerPhone = request.buyerPhone?.ifEmpty { null }
            listing.buyerAgentUserId = request.buyerAgentUserId
            listing.transactionDate = request.transactionDate
            listing.transactionPrice = request.transactionPrice
            listing.finalSellerAgentCommissionAmount = computeCommission(
                request.transactionPrice,
                listing.sellerAgentCommission
            )
            listing.finalBuyerAgentCommissionAmount =
                if (listing.buyerAgentUserId != null && listing.buyerAgentUserId != listing.sellerAgentUserId) {
                    computeCommission(
                        request.transactionPrice,
                        listing.buyerAgentCommission
                    )
                } else {
                    null
                }
        } else {
            listing.buyerName = null
            listing.buyerEmail = null
            listing.buyerPhone = null
            listing.buyerAgentUserId = null
            listing.transactionDate = null
            listing.transactionPrice = null
            listing.finalBuyerAgentCommissionAmount = null
            listing.finalSellerAgentCommissionAmount = null
        }
        save(listing)

        // Record the status
        statusDao.save(
            ListingStatusEntity(
                listing = listing,
                status = request.status,
                comment = request.comment,
                createdAt = now,
                createdById = securityService.getCurrentUserIdOrNull(),
            )
        )

        return listing
    }

    private fun computeCommission(price: Long?, percent: Double?): Long {
        return ((price ?: 0) * (percent ?: 0.0) / 100.0).toLong()
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

    private fun throwInvalidStatus(message: String) {
        throw ConflictException(
            Error(
                code = ErrorCode.LISTING_INVALID_STATUS,
                message = message
            )
        )
    }
}
