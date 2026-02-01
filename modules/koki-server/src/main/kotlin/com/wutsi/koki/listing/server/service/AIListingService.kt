package com.wutsi.koki.listing.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.listing.dto.CreateAIListingRequest
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.server.domain.AIListingEntity
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ai.ListingAgentFactory
import com.wutsi.koki.listing.server.service.ai.ListingContentParserResult
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper

@Service
class AIListingService(
    private val agentFactory: ListingAgentFactory,
    private val locationService: LocationService,
    private val userService: UserService,
    private val jsonMapper: JsonMapper,
    private val listingService: ListingService,
    private val tenantService: TenantService,
    private val aiStorage: AIListingStorage,
) {
    fun getByListing(listingId: Long, tenant: Long): AIListingEntity {
        val listing = listingService.get(listingId, tenant)
        return aiStorage.get(listing)
    }

    fun create(request: CreateAIListingRequest, tenantId: Long): ListingEntity {
        val result = parse(request, tenantId)
        val listing = createListing(request, result, tenantId)
        aiStorage.store(request, result, listing)
        return listing
    }

    private fun parse(request: CreateAIListingRequest, tenantId: Long): ListingContentParserResult {
        val defaultCity = locationService.get(request.cityId)
        val agentUser = request.sellerAgentUserId?.let { id -> userService.get(id, tenantId) }
        val agent = agentFactory.createListingContentParserAgent(defaultCity, agentUser)
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
        val neighbourhoodIdAndCityId = resolveNeighborhoodIdAndCityId(
            neighbourhoodId = result.neighbourhoodId,
            cityId = request.cityId,
        )

        return listingService.create(
            request = CreateListingRequest(
                listingType = result.listingType,
                propertyType = result.propertyType,
                bedrooms = result.bedrooms,
                bathrooms = result.bathrooms,
                halfBathrooms = result.halfBathrooms,
                floors = result.floors,
                units = result.units,
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
                revenue = result.revenue,
                currency = result.currency ?: tenantService.get(tenantId).currency,

                leaseTerm = result.leaseTerm,
                noticePeriod = result.noticePeriod,
                advanceRent = result.advanceRent,
                securityDeposit = result.securityDeposit,

                address = Address(
                    street = result.street,
                    neighborhoodId = neighbourhoodIdAndCityId.first,
                    cityId = neighbourhoodIdAndCityId.second,
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

    private fun resolveNeighborhoodIdAndCityId(neighbourhoodId: Long?, cityId: Long?): Pair<Long?, Long?> {
        val ids = listOfNotNull(neighbourhoodId, cityId)
        if (ids.isEmpty()) {
            return Pair(null, null)
        }

        val locations = locationService.search(
            ids = ids,
            limit = ids.size
        )

        val neighbourhood = locations.find { it.type == LocationType.NEIGHBORHOOD }
        if (neighbourhood != null) {
            return Pair(neighbourhood.id, neighbourhood.parentId)
        }

        val city = locations.find { it.type == LocationType.CITY }
        return Pair(null, city?.id)
    }
}
