package com.wutsi.koki.portal.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.RefDataFixtures.cities
import com.wutsi.koki.RefDataFixtures.locations
import com.wutsi.koki.RefDataFixtures.neighborhoods
import com.wutsi.koki.RoomFixtures.room
import com.wutsi.koki.TenantFixtures.tenants
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.GetRoomResponse
import com.wutsi.koki.room.dto.LeaseTerm
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.UpdateRoomRequest
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class EditRoomControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            ResponseEntity(
                GetRoomResponse(room.copy(status = RoomStatus.PUBLISHED)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetRoomResponse::class.java)
            )
    }

    @Test
    fun `edit published room`() {
        navigateTo("/rooms/${room.id}/edit")
        assertCurrentPageIs(PageName.ROOM_EDIT)

        input("#title", "Premium Room")
        input("#summary", "This is a nice room for 3")
        select("#type", 1)
        select("#leaseType", 1)
        select("#furnishedType", 2)
        select("#numberOfRooms", 3)
        select("#numberOfBathrooms", 4)
        select("#numberOfBeds", 5)
        scroll(.25)
        select("#maxGuests", 6)
        input("#area", "1500")
        input("#pricePerNight", "75")
        assertElementHasAttribute("#pricePerMonth", "disabled")
        assertElementHasAttribute("#visitFees", "disabled")
        select("#checkinTime", 14)
        scroll(.25)
        select("#checkoutTime", 10)
        assertElementHasAttribute("#leaseTerm", "disabled")
        assertElementHasAttribute("#leaseTermDuration", "disabled")
        assertElementHasAttribute("#dateOfAvailability", "disabled")
        select("#yearOfConstruction", 10)
        scrollToBottom()
        select("#country", 3)
        select2("#cityId", "${locations[2].name}, ${locations[0].name}")
        select2("#neighborhoodId", "${neighborhoods[0].name}, ${cities[0].name}")
        input("#street", "340 Nicolet")
        input("#postalCode", "HzH zHz")
        input("#description", "This is a description")
        click("button[type=submit]")

        val request = argumentCaptor<UpdateRoomRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/rooms/${room.id}"), request.capture(), eq(Any::class.java)
        )
        assertEquals("Premium Room", request.firstValue.title)
        assertEquals("This is a nice room for 3", request.firstValue.summary)
        assertEquals(RoomType.HOUSE, request.firstValue.type)
        assertEquals(LeaseType.SHORT_TERM, request.firstValue.leaseType)
        assertEquals(LeaseTerm.UNKNOWN, request.firstValue.leaseTerm)
        assertEquals(FurnishedType.SEMI_FURNISHED, request.firstValue.furnishedType)
        assertEquals(4, request.firstValue.numberOfRooms)
        assertEquals(5, request.firstValue.numberOfBathrooms)
        assertEquals(5, request.firstValue.numberOfBeds)
        assertEquals(6, request.firstValue.maxGuests)
        assertEquals(75.0, request.firstValue.pricePerNight)
        assertEquals(null, request.firstValue.pricePerMonth)
        assertEquals(null, request.firstValue.visitFees)
        assertEquals(tenants[0].currency, request.firstValue.currency)
        assertEquals(locations[2].id, request.firstValue.cityId)
        assertEquals(neighborhoods[0].id, request.firstValue.neighborhoodId)
        assertEquals("340 Nicolet", request.firstValue.street)
        assertEquals("HzH zHz", request.firstValue.postalCode)
        assertEquals("13:00", request.firstValue.checkinTime)
        assertEquals("09:00", request.firstValue.checkoutTime)
        assertEquals("This is a description", request.firstValue.description)
        assertEquals(null, request.firstValue.leaseTermDuration)
        assertEquals(null, request.firstValue.dateOfAvailability)
        assertEquals(1909, request.firstValue.yearOfConstruction)

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `edit draft room`() {
        doReturn(
            ResponseEntity(
                GetRoomResponse(room.copy(status = RoomStatus.DRAFT)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetRoomResponse::class.java)
            )

        navigateTo("/rooms/${room.id}/edit")
        assertCurrentPageIs(PageName.ROOM_EDIT)

        assertElementNotPresent("#title")
        select("#type", 1)
        select("#leaseType", 1)
        select("#furnishedType", 2)
        select("#numberOfRooms", 3)
        select("#numberOfBathrooms", 4)
        select("#numberOfBeds", 5)
        scroll(.25)
        select("#maxGuests", 6)
        input("#area", "1500")
        input("#pricePerNight", "75")
        assertElementHasAttribute("#pricePerMonth", "disabled")
        assertElementHasAttribute("#visitFees", "disabled")
        select("#checkinTime", 14)
        scroll(.25)
        select("#checkoutTime", 10)
        assertElementHasAttribute("#leaseTerm", "disabled")
        assertElementHasAttribute("#leaseTermDuration", "disabled")
        assertElementHasAttribute("#dateOfAvailability", "disabled")
        select("#yearOfConstruction", 10)
        scrollToBottom()
        select("#country", 3)
        select2("#cityId", "${locations[2].name}, ${locations[0].name}")
        select2("#neighborhoodId", "${neighborhoods[0].name}, ${cities[0].name}")
        input("#street", "340 Nicolet")
        input("#postalCode", "HzH zHz")
        click("button[type=submit]")

        val request = argumentCaptor<UpdateRoomRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/rooms/${room.id}"), request.capture(), eq(Any::class.java)
        )
        assertEquals(null, request.firstValue.title)
        assertEquals(null, request.firstValue.summary)
        assertEquals(RoomType.HOUSE, request.firstValue.type)
        assertEquals(LeaseType.SHORT_TERM, request.firstValue.leaseType)
        assertEquals(LeaseTerm.UNKNOWN, request.firstValue.leaseTerm)
        assertEquals(FurnishedType.SEMI_FURNISHED, request.firstValue.furnishedType)
        assertEquals(4, request.firstValue.numberOfRooms)
        assertEquals(5, request.firstValue.numberOfBathrooms)
        assertEquals(5, request.firstValue.numberOfBeds)
        assertEquals(6, request.firstValue.maxGuests)
        assertEquals(75.0, request.firstValue.pricePerNight)
        assertEquals(null, request.firstValue.pricePerMonth)
        assertEquals(null, request.firstValue.visitFees)
        assertEquals(tenants[0].currency, request.firstValue.currency)
        assertEquals(locations[2].id, request.firstValue.cityId)
        assertEquals(neighborhoods[0].id, request.firstValue.neighborhoodId)
        assertEquals("340 Nicolet", request.firstValue.street)
        assertEquals("HzH zHz", request.firstValue.postalCode)
        assertEquals("13:00", request.firstValue.checkinTime)
        assertEquals("09:00", request.firstValue.checkoutTime)
        assertEquals(null, request.firstValue.description)
        assertEquals(null, request.firstValue.leaseTermDuration)
        assertEquals(null, request.firstValue.dateOfAvailability)
        assertEquals(1909, request.firstValue.yearOfConstruction)

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `edit - with full_access permission`() {
        setupUserWithFullAccessPermissions("room")

        navigateTo("/rooms/${room.id}/edit")
        assertCurrentPageIs(PageName.ROOM_EDIT)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(), any<UpdateRoomRequest>(), eq(Any::class.java)
        )

        navigateTo("/rooms/${room.id}/edit")
        select("#type", 2)
        select("#leaseType", 2)
        select("#furnishedType", 2)
        select("#numberOfRooms", 3)
        select("#numberOfBathrooms", 4)
        select("#numberOfBeds", 5)
        scroll(.25)
        select("#maxGuests", 6)
        input("#area", "1500")
        assertElementHasAttribute("#pricePerNight", "disabled")
        input("#pricePerMonth", "500")
        assertElementHasAttribute("#checkinTime", "disabled")
        assertElementHasAttribute("#checkinTime", "disabled")
        scrollToBottom()
        select("#country", 3)
        select2("#cityId", "${locations[2].name}, ${locations[0].name}")
        select2("#neighborhoodId", "${neighborhoods[0].name}, ${cities[0].name}")
        input("#street", "340 Nicolet")
        input("#postalCode", "HzH zHz")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.ROOM_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun cancel() {
        navigateTo("/rooms/${room.id}/edit")
        select("#type", 2)
        select("#leaseType", 2)
        select("#furnishedType", 2)
        select("#numberOfRooms", 3)
        select("#numberOfBathrooms", 4)
        select("#numberOfBeds", 5)
        scroll(.25)
        select("#maxGuests", 6)
        input("#area", "1500")
        assertElementHasAttribute("#pricePerNight", "disabled")
        input("#pricePerMonth", "500")
        assertElementHasAttribute("#checkinTime", "disabled")
        assertElementHasAttribute("#checkinTime", "disabled")
        scrollToBottom()
        select("#country", 3)
        select2("#cityId", "${locations[2].name}, ${locations[0].name}")
        select2("#neighborhoodId", "${neighborhoods[0].name}, ${cities[0].name}")
        input("#street", "340 Nicolet")
        input("#postalCode", "HzH zHz")
        click(".btn-cancel")

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `edit - without permission room-manage`() {
        setupUserWithoutPermissions(listOf("room:manage"))

        navigateTo("/rooms/${room.id}/edit")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/rooms/${room.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
