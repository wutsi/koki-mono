package com.wutsi.koki.listing.server.service

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.MutationType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Date

class ContentQualityScoreServiceTest {
    private val service = ContentQualityScoreService()

    // General Score Tests - Residential
    @Test
    fun `computeGeneralScore - residential with all fields returns 20`() {
        val listing = ListingEntity(
            propertyCategory = PropertyCategory.RESIDENTIAL,
            propertyType = PropertyType.APARTMENT,
            bedrooms = 3,
            bathrooms = 2,
            halfBathrooms = 1,
            floors = 2,
            basementType = BasementType.FULL,
            level = 1,
            parkingType = ParkingType.GARAGE,
            parkings = 2,
            propertyArea = 100,
            year = 2020,
            furnitureType = FurnitureType.FULLY_FURNISHED,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(20, breakdown.general)
    }

    @Test
    fun `computeGeneralScore - residential with 6 out of 12 fields returns 10`() {
        val listing = ListingEntity(
            propertyCategory = PropertyCategory.RESIDENTIAL,
            propertyType = PropertyType.APARTMENT,
            bedrooms = 3,
            bathrooms = 2,
            halfBathrooms = 1,
            floors = 2,
            basementType = BasementType.FULL,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(10, breakdown.general)
    }

    @Test
    fun `computeGeneralScore - residential with no fields returns 0`() {
        val listing = ListingEntity(
            propertyCategory = PropertyCategory.RESIDENTIAL,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.general)
    }

    // General Score Tests - Land
    @Test
    fun `computeGeneralScore - land with all fields returns 20`() {
        val listing = ListingEntity(
            propertyCategory = PropertyCategory.LAND,
            propertyType = PropertyType.LAND,
            lotArea = 1000,
            fenceType = FenceType.BRICK,
            roadPavement = RoadPavement.ASPHALT,
            distanceFromMainRoad = 100,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(20, breakdown.general)
    }

    @Test
    fun `computeGeneralScore - land with 3 out of 5 fields returns 12`() {
        val listing = ListingEntity(
            propertyCategory = PropertyCategory.LAND,
            propertyType = PropertyType.LAND,
            lotArea = 1000,
            fenceType = FenceType.BRICK,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(12, breakdown.general)
    }

    @Test
    fun `computeGeneralScore - land with no fields returns 0`() {
        val listing = ListingEntity(
            propertyCategory = PropertyCategory.LAND,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.general)
    }

    // General Score Tests - Commercial
    @Test
    fun `computeGeneralScore - commercial with all fields returns 20`() {
        val listing = ListingEntity(
            propertyCategory = PropertyCategory.COMMERCIAL,
            propertyType = PropertyType.OFFICE,
            floors = 5,
            level = 2,
            parkingType = ParkingType.GARAGE,
            parkings = 10,
            propertyArea = 500,
            year = 2020,
            units = 10,
            revenue = 100000,
            furnitureType = FurnitureType.FULLY_FURNISHED,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(20, breakdown.general)
    }

    @Test
    fun `computeGeneralScore - commercial with 5 out of 10 fields returns 10`() {
        val listing = ListingEntity(
            propertyCategory = PropertyCategory.COMMERCIAL,
            propertyType = PropertyType.OFFICE,
            floors = 5,
            level = 2,
            parkingType = ParkingType.GARAGE,
            parkings = 10,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(10, breakdown.general)
    }

    @Test
    fun `computeGeneralScore - commercial with no fields returns 0`() {
        val listing = ListingEntity(
            propertyCategory = PropertyCategory.COMMERCIAL,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.general)
    }

    @Test
    fun `computeGeneralScore - no property category returns 0`() {
        val listing = ListingEntity()

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.general)
    }

    // Legal Score Tests
    @Test
    fun `computeLegalScore - all fields filled returns 10`() {
        val listing = ListingEntity(
            landTitle = true,
            technicalFile = true,
            subdivided = false,
            morcelable = true,
            numberOfSigners = 2,
            transactionWithNotary = true,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(10, breakdown.legal)
    }

    @Test
    fun `computeLegalScore - 3 out of 6 fields returns 5`() {
        val listing = ListingEntity(
            landTitle = true,
            technicalFile = true,
            subdivided = false,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(5, breakdown.legal)
    }

    @Test
    fun `computeLegalScore - no fields returns 0`() {
        val listing = ListingEntity()

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.legal)
    }

    // Amenities Score Tests
    @Test
    fun `computeAmenitiesScore - 5 amenities returns 5`() {
        val listing = ListingEntity(
            amenities = mutableListOf(
                AmenityEntity(id = 1),
                AmenityEntity(id = 2),
                AmenityEntity(id = 3),
                AmenityEntity(id = 4),
                AmenityEntity(id = 5),
            ),
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(5, breakdown.amenities)
    }

    @Test
    fun `computeAmenitiesScore - 10 amenities returns 10`() {
        val listing = ListingEntity(
            amenities = mutableListOf(
                AmenityEntity(id = 1),
                AmenityEntity(id = 2),
                AmenityEntity(id = 3),
                AmenityEntity(id = 4),
                AmenityEntity(id = 5),
                AmenityEntity(id = 6),
                AmenityEntity(id = 7),
                AmenityEntity(id = 8),
                AmenityEntity(id = 9),
                AmenityEntity(id = 10),
            ),
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(10, breakdown.amenities)
    }

    @Test
    fun `computeAmenitiesScore - 15 amenities capped at 10`() {
        val listing = ListingEntity(
            amenities = mutableListOf(
                AmenityEntity(id = 1),
                AmenityEntity(id = 2),
                AmenityEntity(id = 3),
                AmenityEntity(id = 4),
                AmenityEntity(id = 5),
                AmenityEntity(id = 6),
                AmenityEntity(id = 7),
                AmenityEntity(id = 8),
                AmenityEntity(id = 9),
                AmenityEntity(id = 10),
                AmenityEntity(id = 11),
                AmenityEntity(id = 12),
                AmenityEntity(id = 13),
                AmenityEntity(id = 14),
                AmenityEntity(id = 15),
            ),
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(10, breakdown.amenities)
    }

    @Test
    fun `computeAmenitiesScore - no amenities returns 0`() {
        val listing = ListingEntity()

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.amenities)
    }

    // Address Score Tests
    @Test
    fun `computeAddressScore - all fields returns 5`() {
        val listing = ListingEntity(
            street = "123 Main St",
            neighbourhoodId = 1,
            cityId = 1,
            country = "US",
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(5, breakdown.address)
    }

    @Test
    fun `computeAddressScore - 2 out of 4 fields returns 2`() {
        val listing = ListingEntity(
            street = "123 Main St",
            cityId = 1,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(2, breakdown.address)
    }

    @Test
    fun `computeAddressScore - no fields returns 0`() {
        val listing = ListingEntity()

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.address)
    }

    // Geo Score Tests
    @Test
    fun `computeGeoScore - both lat and lng present returns 15`() {
        val listing = ListingEntity(
            latitude = 45.5,
            longitude = -73.5,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(15, breakdown.geo)
    }

    @Test
    fun `computeGeoScore - only latitude present returns 0`() {
        val listing = ListingEntity(
            latitude = 45.5,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.geo)
    }

    @Test
    fun `computeGeoScore - only longitude present returns 0`() {
        val listing = ListingEntity(
            longitude = -73.5,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.geo)
    }

    @Test
    fun `computeGeoScore - no coordinates returns 0`() {
        val listing = ListingEntity()

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.geo)
    }

    // Rental Score Tests
    @Test
    fun `computeRentalScore - rental with all fields returns 10`() {
        val listing = ListingEntity(
            listingType = ListingType.RENTAL,
            leaseTerm = 12,
            advanceRent = 1,
            securityDeposit = 1,
            noticePeriod = 30,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(10, breakdown.rental)
    }

    @Test
    fun `computeRentalScore - rental with 2 out of 4 fields returns 5`() {
        val listing = ListingEntity(
            listingType = ListingType.RENTAL,
            leaseTerm = 12,
            advanceRent = 1,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(5, breakdown.rental)
    }

    @Test
    fun `computeRentalScore - rental with no fields returns 0`() {
        val listing = ListingEntity(
            listingType = ListingType.RENTAL,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.rental)
    }

    @Test
    fun `computeRentalScore - non-rental returns 10`() {
        val listing = ListingEntity(
            listingType = ListingType.SALE,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(10, breakdown.rental)
    }

    @Test
    fun `computeRentalScore - listing type null returns 10`() {
        val listing = ListingEntity()

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(10, breakdown.rental)
    }

    // Images Score Tests
    @Test
    fun `computeImagesScore - 20 images with AIQS 4 returns 30`() {
        val listing = ListingEntity(
            averageImageQualityScore = 4.0,
        )

        val breakdown = service.computeBreakdown(listing, 20)
        assertEquals(30, breakdown.images)
    }

    @Test
    fun `computeImagesScore - 10 images with AIQS 4 returns 15`() {
        val listing = ListingEntity(
            averageImageQualityScore = 4.0,
        )

        val breakdown = service.computeBreakdown(listing, 10)
        assertEquals(15, breakdown.images)
    }

    @Test
    fun `computeImagesScore - 20 images with AIQS 2 returns 15`() {
        val listing = ListingEntity(
            averageImageQualityScore = 2.0,
        )

        val breakdown = service.computeBreakdown(listing, 20)
        assertEquals(15, breakdown.images)
    }

    @Test
    fun `computeImagesScore - 10 images with AIQS 2 returns 7`() {
        val listing = ListingEntity(
            averageImageQualityScore = 2.0,
        )

        val breakdown = service.computeBreakdown(listing, 10)
        assertEquals(7, breakdown.images)
    }

    @Test
    fun `computeImagesScore - 30 images capped at 20 with AIQS 4 returns 30`() {
        val listing = ListingEntity(
            averageImageQualityScore = 4.0,
        )

        val breakdown = service.computeBreakdown(listing, 30)
        assertEquals(30, breakdown.images)
    }

    @Test
    fun `computeImagesScore - 0 images returns 0`() {
        val listing = ListingEntity(
            averageImageQualityScore = 4.0,
        )

        val breakdown = service.computeBreakdown(listing, 0)
        assertEquals(0, breakdown.images)
    }

    @Test
    fun `computeImagesScore - null AIQS returns 0`() {
        val listing = ListingEntity(
            averageImageQualityScore = null,
        )

        val breakdown = service.computeBreakdown(listing, 20)
        assertEquals(0, breakdown.images)
    }

    @Test
    fun `computeImagesScore - AIQS 0 returns 0`() {
        val listing = ListingEntity(
            averageImageQualityScore = 0.0,
        )

        val breakdown = service.computeBreakdown(listing, 20)
        assertEquals(0, breakdown.images)
    }

    @Test
    fun `computeImagesScore - 5 images with AIQS 3 returns 5`() {
        val listing = ListingEntity(
            averageImageQualityScore = 3.0,
        )

        val breakdown = service.computeBreakdown(listing, 5)
        // (5/20) * 30 * (3/4) = 0.25 * 30 * 0.75 = 5.625 -> 5
        assertEquals(5, breakdown.images)
    }

    // Integration Tests
    @Test
    fun `compute - complete listing returns 100`() {
        val listing = ListingEntity(
            // General (20)
            propertyCategory = PropertyCategory.RESIDENTIAL,
            propertyType = PropertyType.APARTMENT,
            bedrooms = 3,
            bathrooms = 2,
            halfBathrooms = 1,
            floors = 2,
            basementType = BasementType.FULL,
            level = 1,
            parkingType = ParkingType.GARAGE,
            parkings = 2,
            propertyArea = 100,
            year = 2020,
            furnitureType = FurnitureType.FULLY_FURNISHED,
            // Legal (10)
            landTitle = true,
            technicalFile = true,
            subdivided = false,
            morcelable = true,
            numberOfSigners = 2,
            transactionWithNotary = true,
            // Amenities (10)
            amenities = mutableListOf(
                AmenityEntity(id = 1),
                AmenityEntity(id = 2),
                AmenityEntity(id = 3),
                AmenityEntity(id = 4),
                AmenityEntity(id = 5),
                AmenityEntity(id = 6),
                AmenityEntity(id = 7),
                AmenityEntity(id = 8),
                AmenityEntity(id = 9),
                AmenityEntity(id = 10),
            ),
            // Address (5)
            street = "123 Main St",
            neighbourhoodId = 1,
            cityId = 1,
            country = "US",
            // Geo (15)
            latitude = 45.5,
            longitude = -73.5,
            // Rental (10) - non-rental
            listingType = ListingType.SALE,
            // Images (30) - 20 images with AIQS 4
            averageImageQualityScore = 4.0,
        )

        val score = service.compute(listing, 20)
        assertEquals(100, score)
    }

    @Test
    fun `compute - minimal listing returns low score`() {
        val listing = ListingEntity(
            // Only minimal data
            listingType = ListingType.SALE, // Rental: 10
        )

        val score = service.compute(listing, 0)
        assertEquals(10, score)
    }

    @Test
    fun `computeBreakdown - returns correct breakdown structure`() {
        val listing = ListingEntity(
            propertyCategory = PropertyCategory.RESIDENTIAL,
            propertyType = PropertyType.APARTMENT,
            bedrooms = 3,
            landTitle = true,
            amenities = mutableListOf(
                AmenityEntity(id = 1),
                AmenityEntity(id = 2),
            ),
            street = "123 Main St",
            latitude = 45.5,
            longitude = -73.5,
            listingType = ListingType.SALE,
            averageImageQualityScore = 2.0,
        )

        val breakdown = service.computeBreakdown(listing, 10)

        // General: 2/12 * 20 = 3
        assertEquals(3, breakdown.general)
        // Legal: 1/6 * 10 = 1
        assertEquals(1, breakdown.legal)
        // Amenities: 2
        assertEquals(2, breakdown.amenities)
        // Address: 1/4 * 5 = 1
        assertEquals(1, breakdown.address)
        // Geo: 15
        assertEquals(15, breakdown.geo)
        // Rental: 10
        assertEquals(10, breakdown.rental)
        // Images: (10/20) * 30 * (2/4) = 7
        assertEquals(7, breakdown.images)
        // Total: 3 + 1 + 2 + 1 + 15 + 10 + 7 = 39
        assertEquals(39, breakdown.total)
    }
}
