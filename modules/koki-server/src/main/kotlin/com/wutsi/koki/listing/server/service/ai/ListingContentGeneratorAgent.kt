package com.wutsi.koki.listing.server.service.ai

import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.springframework.http.MediaType

/**
 * AI Agent used for generating listing title/description based on listing details and images.
 * This agent is invoked when a listing is published.
 */
class ListingContentGeneratorAgent(
    val listing: ListingEntity,
    val images: List<FileEntity>,
    val city: LocationEntity?,
    val neighbourhood: LocationEntity?,
    val llm: LLM,
) : Agent(llm, responseType = MediaType.APPLICATION_JSON) {
    companion object {
        const val QUERY = ""
    }

    override fun buildPrompt(query: String, memory: List<String>): String {
        val prompt = this::class.java.getResourceAsStream("/listing/prompt/listing-content-generator.prompt.md")!!
            .reader()
            .readText()
        val amenities = listing.amenities.joinToString(separator = ",") { amenity -> amenity.name }
        var i = 0
        val imageText = images.joinToString(separator = "\n") { image ->
            "- Image ${i++}: ${image.description ?: "No description available"}"
        }

        return prompt.replace("{{listingType}}", listing.listingType?.name ?: "Unknown")
            .replace("{{propertyType}}", listing.propertyType?.name ?: "Unknown")
            .replace("{{bedrooms}}", listing.bedrooms?.toString() ?: "Unknown")
            .replace("{{bathrooms}}", listing.bathrooms?.toString() ?: "Unknown")
            .replace("{{lotArea}}", listing.lotArea?.toString() ?: "Unknown")
            .replace("{{propertyArea}}", listing.propertyArea?.toString() ?: "Unknown")
            .replace("{{street}}", listing.street ?: "Unknown")
            .replace("{{country}}", city?.country?.uppercase() ?: "Unknown")
            .replace("{{city}}", city?.name ?: "Unknown")
            .replace("{{neighbourhood}}", neighbourhood?.name ?: "Unknown")
            .replace("{{additionalInfo}}", listing.publicRemarks ?: "None")
            .replace("{{furnished}}", listing.furnitureType?.name ?: "Unknown")
            .replace("{{hasLandTitle}}", listing.landTitle?.toString() ?: "Unknown")
            .replace("{{hasTechnicalFile}}", listing.technicalFile?.toString() ?: "Unknown")
            .replace("{{transactionWithNotary}}", listing.transactionWithNotary?.toString() ?: "Unknown")
            .replace("{{numberOfSigners}}", listing.numberOfSigners?.toString() ?: "Unknown")
            .replace("{{mutationType}}", listing.mutationType?.name ?: "Unknown")
            .replace("{{subdivided}}", listing.subdivided?.toString() ?: "Unknown")
            .replace("{{morcelable}}", listing.morcelable?.toString() ?: "Unknown")
            .replace("{{amenities}}", amenities)
            .replace("{{images}}", imageText)
            .replace("{{observations}}", memory.joinToString("\n") { entry -> "- $entry" })
    }
}

data class ListingContentGeneratorResult(
    val title: String? = null,
    val summary: String? = null,
    val description: String? = null,
    val titleFr: String? = null,
    val summaryFr: String? = null,
    val descriptionFr: String? = null,
    val heroImageIndex: Int = 0,
)
