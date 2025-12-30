package com.wutsi.koki.listing.server.service.ai

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.MutationType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.http.MediaType
import java.util.Date
import java.util.Locale

class ListingContentParserAgent(
    private val amenityService: AmenityService,
    private val locationService: LocationService,
    private val city: LocationEntity,
    llm: LLM,
) : Agent(llm, responseType = MediaType.APPLICATION_JSON) {
    override fun systemInstructions(): String? = null

    override fun buildPrompt(query: String, memory: List<String>): String {
        val country = Locale("en", city.country).displayCountry

        val prompt = this::class.java.getResourceAsStream("/listing/prompt/listing-content-parser.prompt.md")!!
            .reader()
            .readText()
            .replace("{{query}}", query)
            .replace("{{amenities}}", loadAmenities())
            .replace("{{neighbourhoods}}", loadNeighbourhoods())
            .replace("{{city}}", city.name + "," + country)
            .replace(
                "{{propertyTypes}}",
                PropertyType.entries.filter { it != PropertyType.UNKNOWN }.joinToString(",") { it.name })
            .replace(
                "{{parkingTypes}}",
                ParkingType.entries.filter { it != ParkingType.UNKNOWN }.joinToString(",") { it.name })
            .replace(
                "{{fenceTypes}}",
                FenceType.entries.filter { it != FenceType.UNKNOWN }.joinToString(",") { it.name })
            .replace(
                "{{furnitureTypes}}",
                FurnitureType.entries.filter { it != FurnitureType.UNKNOWN }.joinToString(",") { it.name })
            .replace(
                "{{roadPavements}}",
                RoadPavement.entries.filter { it != RoadPavement.UNKNOWN }.joinToString(",") { it.name })
            .replace(
                "{{mutationTypes}}",
                MutationType.entries.filter { it != MutationType.UNKNOWN }.joinToString(",") { it.name })

        return prompt + memory.joinToString(separator = "\n", prefix = "\n", postfix = "\n")
    }

    private fun loadAmenities(): String {
        return amenityService.search(limit = 200)
            .joinToString(separator = "\n") { amenity -> "${amenity.id},${amenity.name}" }
    }

    private fun loadNeighbourhoods(): String {
        return locationService.search(
            parentId = city.id,
            types = listOf(LocationType.NEIGHBORHOOD),
            limit = 200
        ).joinToString(separator = "\n") { neighbourhood -> "${neighbourhood.id},${neighbourhood.name}" }
    }
}

data class AmenityResult(
    val id: Long = -1,
    val name: String = ""
)

data class ListingContentParserResult(
    val listingType: ListingType? = null,
    val propertyType: PropertyType? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val halfBathrooms: Int? = null,
    val floors: Int? = null,
    val basementType: BasementType? = null,
    val level: Int? = null,
    val parkingType: ParkingType? = null,
    val parkings: Int? = null,
    val fenceType: FenceType? = null,
    val lotArea: Int? = null,
    val propertyArea: Int? = null,
    val year: Int? = null,
    val availableAt: Date? = null,
    val roadPavement: RoadPavement? = null,
    val distanceFromMainRoad: Int? = null,
    val price: Long? = null,
    val visitFees: Long? = null,
    val currency: String? = null,
    val leaseTerm: Int? = null,
    val noticePeriod: Int? = null,
    val advanceRent: Int? = null,
    val securityDeposit: Int? = null,
    val phone: String? = null,
    val furnitureType: FurnitureType? = null,
    val amenities: List<AmenityResult> = emptyList(),
    val street: String? = null,
    val neighbourhood: String? = null,
    val neighbourhoodId: Long? = null,
    val city: String? = null,
    val country: String? = null,
    val valid: Boolean = false,
    val reason: String? = null,
    val publicRemarks: String? = null,
    val commission: Double? = null,
    var landTitle: Boolean? = null,
    val technicalFile: Boolean? = null,
    val numberOfSigners: Int? = null,
    val mutationType: MutationType? = null,
    val transactionWithNotary: Boolean? = null,
    val subdivided: Boolean? = null,
    val morcelable: Boolean? = null,
)
