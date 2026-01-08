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
import com.wutsi.koki.listing.dto.UpdateListingLegalInfoRequest
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
import java.util.Collections.emptyList
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
        minBedrooms: Int? = null,
        maxBedrooms: Int? = null,
        minBathrooms: Int? = null,
        maxBathrooms: Int? = null,
        minPrice: Long? = null,
        maxPrice: Long? = null,
        minSalePrice: Long? = null,
        maxSalePrice: Long? = null,
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
        if (minBedrooms != null) {
            jql.append(" AND L.bedrooms >= :minBedrooms")
        }
        if (maxBedrooms != null) {
            jql.append(" AND L.bedrooms <= :maxBedrooms")
        }
        if (minBathrooms != null) {
            jql.append(" AND L.bathrooms >= :minBathrooms")
        }
        if (maxBathrooms != null) {
            jql.append(" AND L.bathrooms <= :maxBathrooms")
        }
        if (minPrice != null) {
            jql.append(" AND L.price >= :minPrice")
        }
        if (maxPrice != null) {
            jql.append(" AND L.price <= :maxPrice")
        }
        if (minSalePrice != null || maxSalePrice != null) {
            jql.append(" AND L.salePrice IS NOT NULL")
            if (minSalePrice != null) {
                jql.append(" AND L.salePrice >= :minSalePrice")
            }
            if (maxSalePrice != null) {
                jql.append(" AND L.salePrice <= :maxSalePrice")
            }
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
        val orderBy = when (sortBy) {
            ListingSort.NEWEST -> "ORDER BY L.publishedAt DESC, L.createdAt DESC"
            ListingSort.OLDEST -> "ORDER BY L.publishedAt ASC, L.createdAt ASC"
            ListingSort.PRICE_HIGH_LOW -> "ORDER BY L.price DESC"
            ListingSort.PRICE_LOW_HIGH -> "ORDER BY L.price ASC"
            ListingSort.TRANSACTION_DATE -> "ORDER BY L.soldAt DESC, L.publishedAt DESC"
            ListingSort.MODIFIED_DATE -> "ORDER BY L.modifiedAt DESC"
            else -> "ORDER BY L.price ASC"
        }
        jql.append(" $orderBy")

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
        if (minBedrooms != null) {
            query.setParameter("minBedrooms", minBedrooms)
        }
        if (maxBedrooms != null) {
            query.setParameter("maxBedrooms", maxBedrooms)
        }
        if (minBathrooms != null) {
            query.setParameter("minBathrooms", minBathrooms)
        }
        if (maxBathrooms != null) {
            query.setParameter("maxBathrooms", maxBathrooms)
        }
        if (minPrice != null) {
            query.setParameter("minPrice", minPrice)
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice)
        }
        if (minSalePrice != null) {
            query.setParameter("minSalePrice", minSalePrice)
        }
        if (maxSalePrice != null) {
            query.setParameter("maxSalePrice", maxSalePrice)
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
        minBedrooms: Int? = null,
        maxBedrooms: Int? = null,
        minBathrooms: Int? = null,
        maxBathrooms: Int? = null,
        minPrice: Long? = null,
        maxPrice: Long? = null,
        minSalePrice: Long? = null,
        maxSalePrice: Long? = null,
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
        if (minBedrooms != null) {
            jql.append(" AND L.bedrooms >= :minBedrooms")
        }
        if (maxBedrooms != null) {
            jql.append(" AND L.bedrooms <= :maxBedrooms")
        }
        if (minBathrooms != null) {
            jql.append(" AND L.bathrooms >= :minBathrooms")
        }
        if (maxBathrooms != null) {
            jql.append(" AND L.bathrooms <= :maxBathrooms")
        }
        if (minPrice != null) {
            jql.append(" AND L.price >= :minPrice")
        }
        if (maxPrice != null) {
            jql.append(" AND L.price <= :maxPrice")
        }
        if (minSalePrice != null || maxSalePrice != null) {
            jql.append(" AND L.salePrice IS NOT NULL")
            if (minSalePrice != null) {
                jql.append(" AND L.salePrice >= :minSalePrice")
            }
            if (maxSalePrice != null) {
                jql.append(" AND L.salePrice <= :maxSalePrice")
            }
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
        if (minBedrooms != null) {
            query.setParameter("minBedrooms", minBedrooms)
        }
        if (maxBedrooms != null) {
            query.setParameter("maxBedrooms", maxBedrooms)
        }
        if (minBathrooms != null) {
            query.setParameter("minBathrooms", minBathrooms)
        }
        if (maxBathrooms != null) {
            query.setParameter("maxBathrooms", maxBathrooms)
        }
        if (minPrice != null) {
            query.setParameter("minPrice", minPrice)
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice)
        }
        if (minSalePrice != null) {
            query.setParameter("minSalePrice", minSalePrice)
        }
        if (maxSalePrice != null) {
            query.setParameter("maxSalePrice", maxSalePrice)
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

    @Transactional
    fun create(request: CreateListingRequest, tenantId: Long): ListingEntity {
        val now = Date()
        val userId = securityService.getCurrentUserIdOrNull()
        val city = request.address?.cityId?.let { id -> locationService.get(id) }

        val listing = dao.save(
            ListingEntity(
                tenantId = tenantId,
                listingNumber = generateListingNumber(tenantId),
                status = ListingStatus.DRAFT,
                listingType = request.listingType,
                propertyType = request.propertyType,
                propertyCategory = request.propertyType?.category,
                bedrooms = request.bedrooms,
                bathrooms = request.bathrooms,
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
                availableAt = request.availableAt,
                distanceFromMainRoad = request.distanceFromMainRoad,
                roadPavement = request.roadPavement,

                price = request.price,
                visitFees = request.visitFees,
                currency = request.currency?.uppercase()?.ifEmpty { null },
                sellerAgentCommission = request.sellerAgentCommission,
                buyerAgentCommission = request.buyerAgentCommission,
                buyerAgentCommissionAmount = computeCommission(request.price, request.buyerAgentCommission),
                sellerAgentCommissionAmount = computeCommission(request.price, request.sellerAgentCommission),

                leaseTerm = request.leaseTerm,
                noticePeriod = request.noticePeriod,
                securityDeposit = request.securityDeposit,
                advanceRent = request.advanceRent,

                cityId = request.address?.cityId,
                neighbourhoodId = request.address?.neighborhoodId,
                stateId = city?.parentId,
                country = request.address?.country?.lowercase()?.ifEmpty { null },
                street = request.address?.street?.ifEmpty { null },
                postalCode = request.address?.postalCode?.uppercase()?.ifEmpty { null },

                publicRemarks = request.publicRemarks,

                furnitureType = request.furnitureType,
                amenities = if (request.amenityIds.isNotEmpty()) {
                    amenityService.search(
                        ids = request.amenityIds,
                        limit = request.amenityIds.size,
                    ).toMutableList()
                } else {
                    mutableListOf()
                },

                landTitle = request.landTitle,
                technicalFile = request.technicalFile,
                numberOfSigners = request.numberOfSigners,
                mutationType = request.mutationType,
                transactionWithNotary = request.transactionWithNotary,
                subdivided = request.subdivided,
                morcelable = request.morcelable,

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
        listing.propertyCategory = request.propertyType?.category
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
        listing.availableAt = request.availableAt
        listing.distanceFromMainRoad = request.distanceFromMainRoad
        listing.roadPavement = request.roadPavement
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
        listing.buyerAgentCommissionAmount = computeCommission(listing.price, listing.buyerAgentCommission)
        listing.sellerAgentCommissionAmount = computeCommission(listing.price, listing.sellerAgentCommission)
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

        listing.sellerContactId = request.sellerContactId
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
    fun legalInfo(id: Long, request: UpdateListingLegalInfoRequest, tenantId: Long): ListingEntity {
        val userId = securityService.getCurrentUserId()
        val listing = get(id, tenantId)

        listing.landTitle = request.landTitle
        listing.technicalFile = request.technicalFile
        listing.numberOfSigners = request.numberOfSigners
        listing.mutationType = request.mutationType
        listing.transactionWithNotary = request.transactionWithNotary
        listing.subdivided = request.subdivided
        listing.morcelable = request.morcelable
        listing.modifiedAt = Date()
        listing.modifiedById = userId

        dao.save(listing)

        LOGGER.info("Legal information updated for Listing#$id by User#$userId")
        return listing
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
        when (request.status) {
            ListingStatus.SOLD,
            ListingStatus.RENTED -> {
                if (listing.status != ListingStatus.ACTIVE) {
                    throwInvalidStatus("Cannot close a listing in status: ${listing.status}")
                }
            }

            ListingStatus.WITHDRAWN,
            ListingStatus.EXPIRED,
            ListingStatus.CANCELLED -> {
                if (listing.status != ListingStatus.ACTIVE && listing.status != ListingStatus.DRAFT) {
                    throwInvalidStatus("Cannot close a listing in status: ${listing.status}")
                }
            }

            else -> throwInvalidStatus("Invalid status")
        }

        // Update the listing
        val now = Date()
        listing.status = request.status
        listing.closedAt = now
        if (listing.status == ListingStatus.RENTED || listing.status == ListingStatus.SOLD) {
            listing.salePrice = request.salePrice
            listing.soldAt = request.soldAt
            listing.closedOfferId = request.closedOfferId
            listing.buyerAgentUserId = request.buyerAgentUserId
            listing.buyerContactId = request.buyerContactId
            listing.closedOfferId = request.closedOfferId
            listing.finalSellerAgentCommissionAmount = computeCommission(
                request.salePrice,
                listing.sellerAgentCommission,
            )
            if (request.buyerAgentUserId != null && listing.sellerAgentUserId != request.buyerAgentUserId) {
                listing.finalBuyerAgentCommissionAmount = computeCommission(
                    request.salePrice,
                    listing.buyerAgentCommission,
                )
            }
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
