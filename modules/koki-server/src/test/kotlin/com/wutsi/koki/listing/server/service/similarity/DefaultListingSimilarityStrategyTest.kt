package com.wutsi.koki.listing.server.service.similarity

import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.server.domain.ListingEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DefaultListingSimilarityStrategyTest {
    private val strategy = DefaultListingSimilarityStrategy()

    @Test
    fun `perfect match returns 1_0`() {
        val listing = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 500000L
        )

        val score = strategy.computeSimilarity(listing, listing)
        assertEquals(1.0, score, 0.001)
    }

    @Test
    fun `same type and bedrooms but different price within range`() {
        val reference = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 500000L
        )
        val candidate = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 550000L // 10% difference
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Type: 1.0 * 0.5 = 0.5
        // Bedrooms: 1.0 * 0.3 = 0.3
        // Price: (1.0 - 0.1/0.25) * 0.2 = 0.6 * 0.2 = 0.12
        // Total: 0.5 + 0.3 + 0.12 = 0.92
        assertEquals(0.92, score, 0.001)
    }

    @Test
    fun `same type different bedrooms by 1`() {
        val reference = createListing(
            propertyType = PropertyType.APARTMENT,
            bedrooms = 3,
            price = 400000L
        )
        val candidate = createListing(
            propertyType = PropertyType.APARTMENT,
            bedrooms = 4,
            price = 400000L
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Type: 1.0 * 0.5 = 0.5
        // Bedrooms: 0.5 * 0.3 = 0.15
        // Price: 1.0 * 0.2 = 0.2
        // Total: 0.5 + 0.15 + 0.2 = 0.85
        assertEquals(0.85, score, 0.001)
    }

    @Test
    fun `different type returns low score`() {
        val reference = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 500000L
        )
        val candidate = createListing(
            propertyType = PropertyType.APARTMENT,
            bedrooms = 3,
            price = 500000L
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Type: 0.0 * 0.5 = 0.0
        // Bedrooms: 1.0 * 0.3 = 0.3
        // Price: 1.0 * 0.2 = 0.2
        // Total: 0.0 + 0.3 + 0.2 = 0.5
        assertEquals(0.5, score, 0.001)
    }

    @Test
    fun `price outside 25 percent range returns zero price score`() {
        val reference = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 500000L
        )
        val candidate = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 650000L // 30% difference, outside ±25%
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Type: 1.0 * 0.5 = 0.5
        // Bedrooms: 1.0 * 0.3 = 0.3
        // Price: 0.0 * 0.2 = 0.0
        // Total: 0.5 + 0.3 + 0.0 = 0.8
        assertEquals(0.8, score, 0.001)
    }

    @Test
    fun `bedrooms difference of 2 or more returns zero bedroom score`() {
        val reference = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 500000L
        )
        val candidate = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 5, // Difference of 2
            price = 500000L
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Type: 1.0 * 0.5 = 0.5
        // Bedrooms: 0.0 * 0.3 = 0.0
        // Price: 1.0 * 0.2 = 0.2
        // Total: 0.5 + 0.0 + 0.2 = 0.7
        assertEquals(0.7, score, 0.001)
    }

    @Test
    fun `uses sale price when available`() {
        val reference = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 500000L,
            salePrice = 480000L
        )
        val candidate = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 600000L,
            salePrice = 480000L // Same sale price
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Should use sale prices which match perfectly
        assertEquals(1.0, score, 0.001)
    }

    @Test
    fun `price at lower boundary 25 percent`() {
        val reference = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 400000L
        )
        val candidate = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 300000L // Exactly 25% lower
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Type: 1.0 * 0.5 = 0.5
        // Bedrooms: 1.0 * 0.3 = 0.3
        // Price: (1.0 - 1.0) * 0.2 = 0.0
        // Total: 0.5 + 0.3 + 0.0 = 0.8
        assertEquals(0.8, score, 0.001)
    }

    @Test
    fun `price at upper boundary 25 percent`() {
        val reference = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 400000L
        )
        val candidate = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 500000L // Exactly 25% higher
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Type: 1.0 * 0.5 = 0.5
        // Bedrooms: 1.0 * 0.3 = 0.3
        // Price: (1.0 - 1.0) * 0.2 = 0.0
        // Total: 0.5 + 0.3 + 0.0 = 0.8
        assertEquals(0.8, score, 0.001)
    }

    @Test
    fun `null property type returns zero type score`() {
        val reference = createListing(
            propertyType = null,
            bedrooms = 3,
            price = 500000L
        )
        val candidate = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 500000L
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Type: 0.0 * 0.5 = 0.0
        // Bedrooms: 1.0 * 0.3 = 0.3
        // Price: 1.0 * 0.2 = 0.2
        // Total: 0.0 + 0.3 + 0.2 = 0.5
        assertEquals(0.5, score, 0.001)
    }

    @Test
    fun `null bedrooms returns zero bedroom score`() {
        val reference = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = null,
            price = 500000L
        )
        val candidate = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 500000L
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Type: 1.0 * 0.5 = 0.5
        // Bedrooms: 0.0 * 0.3 = 0.0
        // Price: 1.0 * 0.2 = 0.2
        // Total: 0.5 + 0.0 + 0.2 = 0.7
        assertEquals(0.7, score, 0.001)
    }

    @Test
    fun `null price returns zero price score`() {
        val reference = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = null
        )
        val candidate = createListing(
            propertyType = PropertyType.HOUSE,
            bedrooms = 3,
            price = 500000L
        )

        val score = strategy.computeSimilarity(reference, candidate)
        // Type: 1.0 * 0.5 = 0.5
        // Bedrooms: 1.0 * 0.3 = 0.3
        // Price: 0.0 * 0.2 = 0.0
        // Total: 0.5 + 0.3 + 0.0 = 0.8
        assertEquals(0.8, score, 0.001)
    }

    private fun createListing(
        propertyType: PropertyType?,
        bedrooms: Int?,
        price: Long?,
        salePrice: Long? = null,
    ): ListingEntity {
        return ListingEntity(
            propertyType = propertyType,
            bedrooms = bedrooms,
            price = price,
            salePrice = salePrice
        )
    }
}
