package com.wutsi.koki.listing.server.service.agent

import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.springframework.http.MediaType

class ListingDescriptorAgent(
    val listing: ListingEntity,
    val images: List<FileEntity>,
    val city: LocationEntity?,
    val neighbourhood: LocationEntity?,
    val llm: LLM,
) : Agent(llm, responseType = MediaType.APPLICATION_JSON) {
    companion object {
        val SYSTEM_INSTRUCTIONS = """
            You are a real estate agent helping customers to rent or buy properties.
            You analyze all the images provided to provide an accurate and detailed description of the property.

            You must return the property description in JSON that looks like:
            {
              "title": "Title of the property in less than 100 characters",
              "summary": "Short SEO friendly summary in less than 160 characters",
              "description": "A more comprehensive description in less than 500 characters",
              "titleFr": "Title translated in french",
              "summaryFr": "Short summary translated in french",
              "descriptionFr": "Description  translated in french",
              "heroImageIndex": "Index of the hero image that represent best the property, starting at 0. Should be negative if no image analyzed"
            }

            Instructions:
            - Instructions for crafting the title:
              - The title should include ONLY the following information:
                - Type of property
                - Listing type,
                - Number of bedrooms (to include if the property is not a land)
                - Lot area (to include only for land)
                - City
                - Neighbourhood in round brackets
              - Examples:
                - Apartment for rent, 3 bedrooms, Douala (Bonapriso)
                - Land for sale, 1200m2, Douala (Bonapriso)
            - Instructions for crafting the description:
              - Start with Compelling Opening: Your first sentence or two should immediately grab attention and reinforce your unique selling proposition from your title.
              - For fully furnished properties, expand on key Features and amenities: Now's the time to elaborate on the highlights mentioned in your title and introduce other enticing features. Be specific and descriptive.
              - Highlight the experience: Think about what makes your property special. Is it the peace and quiet, the convenience to attractions, the luxurious amenities (for fully furnished properties), or the thoughtful touches you provide?
              - Aim for around 300-400 words.
              - For readability, break the description in paragraph of 2–3 sentences.
            - Instructions for crafting the summary:
              - Expand on the Title and Hook
              - Focus on the Benefits for the Sharer's Audience: Why should someone click on this link? What kind of experience awaits the customer?
              - Use Action-Oriented Language: Encourage clicks and create a sense of desire.
              - Aim for around 150-160 characters.
            - Instructions for French translation:
              - The french abbreviation for "bedroom" is CAC. Example: "1BR" should be translate to "1CAC"
              - The french translation for "bathroom" is CDB. Example: "2BA" should be translate to "2SDB"
        """.trimIndent()

        const val QUERY = "Extract the information from the images provided for this property"
    }

    override fun systemInstructions(): String {
        return SYSTEM_INSTRUCTIONS
    }

    override fun buildPrompt(query: String, memory: List<String>): String {
        val amenities = listing.amenities.joinToString(separator = ",") { amenity -> amenity.name }
        var i = 0
        val imageText = images.joinToString(separator = "\n") { image ->
            "- Image ${i++}: ${image.description ?: "No description available"}"
        }
        return """
            Goal: Create the detailed description of a property listing.
            Query: {{query}}

            Property Information:
            - Type of listing: {{listingType}}
            - Type of property: {{propertyType}}
            - Bedrooms: {{bedrooms}}
            - Bathrooms: {{bathrooms}}
            - Furnished: {{furnished}}
            - Country code: {{country}}
            - Street: {{street}}
            - City: {{city}}
            - Neighbourhood: {{neighbourhood}}
            - Lot area: {{lotArea}}
            - Amenities: {{amenities}}

            Images Description:
            {{images}}

            Observations:
            {{observations}}
        """.trimIndent()
            .replace("{{query}}", query)
            .replace("{{listingType}}", listing.listingType?.name ?: "Unknown")
            .replace("{{propertyType}}", listing.propertyType?.name ?: "Unknown")
            .replace("{{bedrooms}}", listing.bedrooms?.toString() ?: "Unknown")
            .replace("{{bathrooms}}", listing.bathrooms?.toString() ?: "Unknown")
            .replace("{{furnished}}", listing.furnitureType?.name ?: "Unknown")
            .replace("{{street}}", listing.street ?: "Unknown")
            .replace("{{country}}", city?.country?.uppercase() ?: "Unknown")
            .replace("{{city}}", city?.name ?: "Unknown")
            .replace("{{neighbourhood}}", neighbourhood?.name ?: "Unknown")
            .replace("{{lotArea}}", listing.lotArea?.toString() ?: "Unknown")
            .replace("{{amenities}}", amenities)
            .replace("{{images}}", imageText)
            .replace("{{observations}}", memory.joinToString("\n") { entry -> "- $entry" })
    }

    override fun tools(): List<Tool> = emptyList<Tool>()
}
