package com.wutsi.koki.listing.server.service.agent

import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.http.MediaType

class ListingDescriptorAgent(
    val listing: ListingEntity,
    val locationService: LocationService,
    val llm: LLM,
    val maxIterations: Int = 5,
) : Agent(llm, maxIterations, MediaType.APPLICATION_JSON) {
    companion object {
        val SYSTEM_INSTRUCTIONS = """
            You are a real estate agent helping customers to rent or buy properties.
            You analyze all the images provided to provide an accurate and detailed description of the property.

            You must return the property description in JSON that looks like:
            {
              "title": "Title of the property in less than 100 characters",
              "summary": "Short SEO friendly summary in less than 160 characters",
              "description": "A more comprehensive description in less than 1000 characters",
              "titleFr": "Title translated in french",
              "summaryFr": "Short summary translated in french",
              "descriptionFr": "Description  translated in french",
              "heroImageIndex": "Index of the hero image that represent best the property, starting at 0. Should be negative if no image analyzed"
            }

            Instructions:
            - Instructions for crafting the title:
              - Highlight the Unique Selling Proposition: What makes your property stand out? Is it a stunning view, a unique amenity, a prime location, or a special experience? Lead with that!
              - Be Specific and Descriptive: Use keywords that potential guests are likely to search for. Include details about the type of property, location, and key features.
              - Include Location Benefits: Mentioning nearby attractions, neighborhoods, or ease of access can be a big draw.
              - Use Strong and Evocative Language: Words like "stunning," "charming," "luxury," "private," and "unique" can create a more appealing image.
              - Keep it Concise and Readable: Aim for a title that is easy to scan and understand quickly. Aim for a maximum of 50 characters
            - Instructions for crafting the description:
              - Start with Compelling Opening: Your first sentence or two should immediately grab attention and reinforce your unique selling proposition from your title.
              - For fully furnished properties, expand on key Features and amenities: Now's the time to elaborate on the highlights mentioned in your title and introduce other enticing features. Be specific and descriptive.
              - Highlight the experience: Think about what makes your property special. Is it the peace and quiet, the convenience to attractions, the luxurious amenities (for fully furnished properties), or the thoughtful touches you provide?
              - Aim for around 300-400 words.
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
        val amenities = listing.amenities
            .map { amenity -> amenity.name }
            .joinToString(separator = ",")
        val city = listing.cityId?.let { id -> locationService.get(id) }
        val neighbourhood = listing.neighbourhoodId?.let { id -> locationService.get(id) }
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
            - City: {{city}}
            - Neighbourhood: {{neighbourhood}}
            - Amenities: {{amenities}}

            Observations:
            {{observations}}
        """.trimIndent()
            .replace("{{amenities}}", amenities)
            .replace("{{propertyType}}", listing.propertyType?.name ?: "Unknown")
            .replace("{{listingType}}", listing.listingType?.name ?: "Unknown")
            .replace("{{bedrooms}}", listing.bedrooms?.toString() ?: "Unknown")
            .replace("{{bathrooms}}", listing.bathrooms?.toString() ?: "Unknown")
            .replace("{{furnished}}", listing.furnitureType?.name ?: "Unknown")
            .replace("{{country}}", city?.country?.uppercase() ?: "Unknown")
            .replace("{{city}}", city?.name ?: "Unknown")
            .replace("{{neighbourhood}}", neighbourhood?.name ?: "Unknown")
            .replace("{{query}}", query)
            .replace("{{observations}}", memory.map { entry -> "- $entry" }.joinToString("\n"))
    }

    override fun tools(): List<Tool> = emptyList<Tool>()
}
