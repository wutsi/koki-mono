package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ContactFixtures.contacts
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.RefDataFixtures.amenities
import com.wutsi.koki.RefDataFixtures.cities
import com.wutsi.koki.RefDataFixtures.locations
import com.wutsi.koki.RefDataFixtures.neighborhoods
import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.listing.dto.UpdateListingAddressRequest
import com.wutsi.koki.listing.dto.UpdateListingAmenitiesRequest
import com.wutsi.koki.listing.dto.UpdateListingGeoLocationRequest
import com.wutsi.koki.listing.dto.UpdateListingLeasingRequest
import com.wutsi.koki.listing.dto.UpdateListingPriceRequest
import com.wutsi.koki.listing.dto.UpdateListingRemarksRequest
import com.wutsi.koki.listing.dto.UpdateListingRequest
import com.wutsi.koki.listing.dto.UpdateListingSellerRequest
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class EditListingControllerTest : AbstractPageControllerTest() {
    @Test
    fun `general - land`() {
        navigateTo("/listings/edit?id=${listing.id}")
        Thread.sleep(100)

        assertCurrentPageIs(PageName.LISTING_EDIT)
        selectByValue("#propertyType", PropertyType.LAND.name)
        Thread.sleep(100)
        input("#lotArea", "1000")
        select("#fenceType", 1)
        scrollToBottom()
        input("#distanceFromMainRoad", "700")
        select("#roadPavement", 2)
        scrollToBottom()
        click("button[type=submit]")
        val req1 = argumentCaptor<UpdateListingRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}"),
            req1.capture(),
            eq(Any::class.java),
        )
        assertEquals(listing.listingType, req1.firstValue.listingType)
        assertEquals(PropertyType.LAND, req1.firstValue.propertyType)
        assertEquals(null, req1.firstValue.units)
        assertEquals(null, req1.firstValue.bedrooms)
        assertEquals(null, req1.firstValue.bathrooms)
        assertEquals(null, req1.firstValue.halfBathrooms)
        assertEquals(null, req1.firstValue.floors)
        assertEquals(null, req1.firstValue.basementType)
        assertEquals(null, req1.firstValue.level)
        assertEquals(null, req1.firstValue.unit)
        assertEquals(null, req1.firstValue.parkingType)
        assertEquals(null, req1.firstValue.parkings)
        assertEquals(1000, req1.firstValue.lotArea)
        assertEquals(null, req1.firstValue.propertyArea)
        assertEquals(FenceType.NONE, req1.firstValue.fenceType)
        assertEquals(700, req1.firstValue.distanceFromMainRoad)
        assertEquals(RoadPavement.CONCRETE, req1.firstValue.roadPavement)
        assertEquals(null, req1.firstValue.year)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun `general - commercial`() {
        navigateTo("/listings/edit?id=${listing.id}")
        Thread.sleep(100)

        assertCurrentPageIs(PageName.LISTING_EDIT)
        selectByValue("#listingType", ListingType.RENTAL.name)
        selectByValue("#propertyType", PropertyType.BUILDING.name)
        Thread.sleep(100)

        input("#units", "5")
        input("#lotArea", "1000")
        input("#propertyArea", "800")
        scroll(.33)
        input("#floors", "10")
        select("#level", 4)
        input("#parkings", "1")
        select("#parkingType", 2)
        select("#fenceType", 1)
        scrollToBottom()
        select("#basementType", 3)
        input("#distanceFromMainRoad", "1000")
        select("#roadPavement", 2)
        input("#year", "1990")
        click("button[type=submit]")
        val req1 = argumentCaptor<UpdateListingRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}"),
            req1.capture(),
            eq(Any::class.java),
        )
        assertEquals(ListingType.RENTAL, req1.firstValue.listingType)
        assertEquals(PropertyType.BUILDING, req1.firstValue.propertyType)
        assertEquals(5, req1.firstValue.units)
        assertEquals(null, req1.firstValue.bedrooms)
        assertEquals(null, req1.firstValue.bathrooms)
        assertEquals(null, req1.firstValue.halfBathrooms)
        assertEquals(10, req1.firstValue.floors)
        assertEquals(BasementType.PART, req1.firstValue.basementType)
        assertEquals(2, req1.firstValue.level)
        assertEquals(null, req1.firstValue.unit)
        assertEquals(ParkingType.GARAGE, req1.firstValue.parkingType)
        assertEquals(1, req1.firstValue.parkings)
        assertEquals(1000, req1.firstValue.lotArea)
        assertEquals(800, req1.firstValue.propertyArea)
        assertEquals(1000, req1.firstValue.distanceFromMainRoad)
        assertEquals(RoadPavement.CONCRETE, req1.firstValue.roadPavement)
        assertEquals(1990, req1.firstValue.year)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun `general - residential`() {
        navigateTo("/listings/edit?id=${listing.id}")
        Thread.sleep(100)

        assertCurrentPageIs(PageName.LISTING_EDIT)
        selectByValue("#listingType", ListingType.RENTAL.name)
        selectByValue("#propertyType", PropertyType.DUPLEX.name)
        Thread.sleep(100)

        input("#bedrooms", "4")
        input("#bathrooms", "2")
        input("#halfBathrooms", "1")
        input("#lotArea", "1000")
        input("#propertyArea", "800")
        scroll(.33)
        input("#floors", "10")
        select("#level", 4)
        input("#parkings", "1")
        select("#parkingType", 2)
        scroll(.33)
        select("#fenceType", 1)
        select("#basementType", 3)
        input("#distanceFromMainRoad", "1000")
        select("#roadPavement", 2)
        input("#year", "1990")
        scrollToBottom()
        click("button[type=submit]")
        val req1 = argumentCaptor<UpdateListingRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}"),
            req1.capture(),
            eq(Any::class.java),
        )
        assertEquals(ListingType.RENTAL, req1.firstValue.listingType)
        assertEquals(PropertyType.DUPLEX, req1.firstValue.propertyType)
        assertEquals(null, req1.firstValue.units)
        assertEquals(4, req1.firstValue.bedrooms)
        assertEquals(2, req1.firstValue.bathrooms)
        assertEquals(1, req1.firstValue.halfBathrooms)
        assertEquals(10, req1.firstValue.floors)
        assertEquals(BasementType.PART, req1.firstValue.basementType)
        assertEquals(2, req1.firstValue.level)
        assertEquals(null, req1.firstValue.unit)
        assertEquals(ParkingType.GARAGE, req1.firstValue.parkingType)
        assertEquals(1, req1.firstValue.parkings)
        assertEquals(1000, req1.firstValue.lotArea)
        assertEquals(800, req1.firstValue.propertyArea)
        assertEquals(1000, req1.firstValue.distanceFromMainRoad)
        assertEquals(RoadPavement.CONCRETE, req1.firstValue.roadPavement)
        assertEquals(1990, req1.firstValue.year)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun amenities() {
        navigateTo("/listings/edit/amenities?id=${listing.id}")

        assertCurrentPageIs(PageName.LISTING_EDIT_AMENITIES)
        select("#furnitureType", 1)
        click("#chk-amenity-" + amenities[9].id)
        click("#chk-amenity-" + amenities[10].id)
        click("#chk-amenity-" + amenities[11].id)
        click("button[type=submit]")
        val req2 = argumentCaptor<UpdateListingAmenitiesRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/amenities"),
            req2.capture(),
            eq(Any::class.java),
        )
        assertEquals(FurnitureType.UNFURNISHED, req2.firstValue.furnitureType)
        assertEquals(3 + listing.amenityIds.size, req2.firstValue.amenityIds.size)
        assertEquals(true, req2.firstValue.amenityIds.contains(listing.amenityIds[0]))
        assertEquals(true, req2.firstValue.amenityIds.contains(listing.amenityIds[1]))
        assertEquals(true, req2.firstValue.amenityIds.contains(listing.amenityIds[2]))
        assertEquals(true, req2.firstValue.amenityIds.contains(listing.amenityIds[3]))
        assertEquals(true, req2.firstValue.amenityIds.contains(amenities[9].id))
        assertEquals(true, req2.firstValue.amenityIds.contains(amenities[10].id))
        assertEquals(true, req2.firstValue.amenityIds.contains(amenities[11].id))

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun address() {
        navigateTo("/listings/edit/address?id=${listing.id}")

        assertCurrentPageIs(PageName.LISTING_EDIT_ADDRESS)
        select("#country", 3)
        select2("#cityId", "${locations[2].name}, ${locations[0].name}")
        select2("#neighbourhoodId", "${neighborhoods[0].name}, ${cities[0].name}")
        input("#street", "340 Pascal")
        click("button[type=submit]")
        val req3 = argumentCaptor<UpdateListingAddressRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/address"),
            req3.capture(),
            eq(Any::class.java),
        )
        assertEquals("DZ", req3.firstValue.address?.country)
        assertEquals(locations[2].id, req3.firstValue.address?.cityId)
        assertEquals(neighborhoods[0].id, req3.firstValue.address?.neighborhoodId)
        assertEquals("340 Pascal", req3.firstValue.address?.street)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun geoLocation() {
        navigateTo("/listings/edit/geo-location?id=${listing.id}")

        assertCurrentPageIs(PageName.LISTING_EDIT_GEOLOCATION)
        scrollToBottom()
        click("button[type=submit]")
        val req4 = argumentCaptor<UpdateListingGeoLocationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/geo-location"),
            req4.capture(),
            eq(Any::class.java),
        )
        assertEquals(listing.geoLocation?.latitude, req4.firstValue.geoLocation?.latitude)
        assertEquals(listing.geoLocation?.longitude, req4.firstValue.geoLocation?.longitude)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun remarks() {
        navigateTo("/listings/edit/remarks?id=${listing.id}")

        assertCurrentPageIs(PageName.LISTING_EDIT_REMARK)
        input("#publicRemarks", "These are public remarks")
        input("#agentRemarks", "Remarks from agent")
        scrollToBottom()
        click("button[type=submit]")
        val req6 = argumentCaptor<UpdateListingRemarksRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/remarks"),
            req6.capture(),
            eq(Any::class.java),
        )
        assertEquals("These are public remarks", req6.firstValue.publicRemarks)
        assertEquals("Remarks from agent", req6.firstValue.agentRemarks)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun price() {
        navigateTo("/listings/edit/price?id=${listing.id}")

        assertCurrentPageIs(PageName.LISTING_EDIT_PRICE)
        input("#price", "180000")
        input("#revenue", "70000")
        input("#visitFees", "5")
        input("#sellerAgentCommission", "6.5")
        scrollToBottom()
        // input("#buyerAgentCommission", "3.0")
        click("button[type=submit]")
        val req7 = argumentCaptor<UpdateListingPriceRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/price"),
            req7.capture(),
            eq(Any::class.java),
        )
        assertEquals(180000, req7.firstValue.price)
        assertEquals(70000, req7.firstValue.revenue)
        assertEquals(5, req7.firstValue.visitFees)
        assertEquals(6.5, req7.firstValue.sellerAgentCommission)
        assertEquals(null, req7.firstValue.buyerAgentCommission)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun leasing() {
        navigateTo("/listings/edit/leasing?id=${listing.id}")

        assertCurrentPageIs(PageName.LISTING_EDIT_LEASING)
        select("#securityDeposit", 2)
        select("#advanceRent", 3)
        select("#leaseTerm", 4)
        scrollToBottom()
        select("#noticePeriod", 5)
        click("button[type=submit]")
        val req8 = argumentCaptor<UpdateListingLeasingRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/leasing"),
            req8.capture(),
            eq(Any::class.java),
        )
        assertEquals(2, req8.firstValue.securityDeposit)
        assertEquals(3, req8.firstValue.advanceRent)
        assertEquals(4, req8.firstValue.leaseTerm)
        assertEquals(5, req8.firstValue.noticePeriod)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun seller() {
        navigateTo("/listings/edit/seller?id=${listing.id}")

        assertCurrentPageIs(PageName.LISTING_EDIT_SELLER)
        select2("#sellerContactId", contacts[1].firstName + " " + contacts[1].lastName)
        click("button[type=submit]")
        val req8 = argumentCaptor<UpdateListingSellerRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/seller"),
            req8.capture(),
            eq(Any::class.java),
        )
        assertEquals(contacts[1].id, req8.firstValue.sellerContactId)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun `without manage AND full_access permission`() {
        setupUserWithoutPermissions(listOf("listing:manage", "listing:full_access"))

        navigateTo("/listings/edit?id=${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `edit another agent listing`() {
        setupUserWithoutPermissions(listOf("listing:full_access"))
        doReturn(
            ResponseEntity(
                GetListingResponse(listing.copy(sellerAgentUserId = 9999)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        navigateTo("/listings/edit?id=${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `ACTIVE listing`() {
        setupListing(ListingStatus.ACTIVE)

        navigateTo("/listings/edit?id=${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `RENTED listing`() {
        setupListing(ListingStatus.RENTED)

        navigateTo("/listings/edit?id=${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    private fun setupListing(
        status: ListingStatus = ListingStatus.ACTIVE,
        propertyType: PropertyType = PropertyType.APARTMENT,
        listingType: ListingType = ListingType.RENTAL,
    ): Long {
        doReturn(
            ResponseEntity(
                GetListingResponse(
                    listing.copy(status = status, propertyType = propertyType, listingType = listingType)
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )
        return listing.id
    }
}
