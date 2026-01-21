package com.wutsi.koki.listing.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.listing.dto.CreateAIListingRequest
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.server.dao.AIListingRepository
import com.wutsi.koki.listing.server.domain.AIListingEntity
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ai.ListingAgentFactory
import com.wutsi.koki.listing.server.service.ai.ListingContentParserResult
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.service.TenantService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper

@Service
class AIListingService(
    private val agentFactory: ListingAgentFactory,
    private val locationService: LocationService,
    private val jsonMapper: JsonMapper,
    private val listingService: ListingService,
    private val tenantService: TenantService,
    private val dao: AIListingRepository,
) {
    @Transactional
    fun create(request: CreateAIListingRequest, tenantId: Long): ListingEntity {
        val result = parse(request)
        val listing = createListing(request, result, tenantId)
        storePrompt(request, result, listing)
        return listing
    }

    private fun parse(request: CreateAIListingRequest): ListingContentParserResult {
        val defaultCity = locationService.get(request.cityId)
        val agent = agentFactory.createListingContentParserAgent(defaultCity)
        val json = agent.run(request.text)

        val result = jsonMapper.readValue(json, ListingContentParserResult::class.java)
        if (!result.valid) {
            throw BadRequestException(
                error = Error(
                    code = ErrorCode.LISTING_INVALID_TEXT,
                    message = result.reason
                )
            )
        }
        return result
    }

    private fun createListing(
        request: CreateAIListingRequest,
        result: ListingContentParserResult,
        tenantId: Long,
    ): ListingEntity {
        val tenant = tenantService.get(tenantId)

        return listingService.create(
            request = CreateListingRequest(
                listingType = result.listingType,
                propertyType = result.propertyType,
                bedrooms = result.bedrooms,
                bathrooms = result.bathrooms,
                halfBathrooms = result.halfBathrooms,
                floors = result.floors,
                basementType = result.basementType,
                level = result.level,
                parkingType = result.parkingType,
                parkings = result.parkings,
                fenceType = result.fenceType,
                lotArea = result.lotArea,
                propertyArea = result.propertyArea,
                year = result.year,
                availableAt = result.availableAt,
                roadPavement = result.roadPavement,
                distanceFromMainRoad = result.distanceFromMainRoad,

                price = result.price,
                visitFees = result.visitFees,
                currency = result.currency ?: tenant.currency,

                leaseTerm = result.leaseTerm,
                noticePeriod = result.noticePeriod,
                advanceRent = result.advanceRent,
                securityDeposit = result.securityDeposit,

                address = Address(
                    street = result.street,
                    neighborhoodId = result.neighbourhoodId,
                    cityId = request.cityId,
                    country = result.country,
                ),

                publicRemarks = result.publicRemarks,

                furnitureType = result.furnitureType,
                amenityIds = result.amenities.map { amenity -> amenity.id },

                landTitle = result.landTitle,
                technicalFile = result.technicalFile,
                mutationType = result.mutationType,
                numberOfSigners = result.numberOfSigners,
                transactionWithNotary = result.transactionWithNotary,
                subdivided = result.subdivided,
                morcelable = result.morcelable,

                sellerAgentUserId = request.sellerAgentUserId,
            ),
            tenantId = tenantId,
        )
    }

    private fun storePrompt(
        request: CreateAIListingRequest,
        result: ListingContentParserResult,
        listing: ListingEntity,
    ) {
        dao.save(
            AIListingEntity(
                tenantId = listing.tenantId,
                listing = listing,
                text = request.text,
                result = jsonMapper.writeValueAsString(result),
                createdAt = listing.createdAt,
            )
        )
    }
}
