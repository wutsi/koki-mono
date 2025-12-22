package com.wutsi.koki.listing.server.service.ai

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import org.springframework.http.MediaType
import java.util.Date
import java.util.Locale

class ListingParserAgent(
    private val amenityService: AmenityService,
    private val city: LocationEntity,
    llm: LLM,
) : Agent(llm, responseType = MediaType.APPLICATION_JSON) {
    override fun systemInstructions(): String? {
        return null
    }

    override fun buildPrompt(query: String, memory: List<String>): String {
        val prompt = this::class.java.getResourceAsStream("/listing/prompt/listing-parser-agent.prompt.md")!!
            .reader()
            .readText()

        val country = Locale("en", city.country).displayCountry
        return prompt.replace("{{query}}", query)
            .replace("{{amenities}}", loadAmenities())
            .replace("{{city}}", city.name + "," + country)
    }

    override fun tools(): List<Tool> = emptyList<Tool>()

    private fun loadAmenities(): String {
        return amenityService.search(limit = 200)
            .joinToString(separator = "\n") { amenity -> "${amenity.id},${amenity.name}" }
    }
}

data class ListingParserAgentResult(
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
    val amenityIds: List<Long> = emptyList(),
    val street: String? = null,
    val neighbourhood: String? = null,
    val city: String? = null,
    val country: String? = null,
    val valid: Boolean = false,
    val reason: String? = null,
    val hasLandTitle: Boolean? = null,
    val publicRemarks: String? = null,
)
