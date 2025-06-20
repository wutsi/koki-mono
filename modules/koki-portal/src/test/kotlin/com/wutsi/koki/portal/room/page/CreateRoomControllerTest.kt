package com.wutsi.koki.portal.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AccountFixtures.accounts
import com.wutsi.koki.RefDataFixtures.cities
import com.wutsi.koki.RefDataFixtures.locations
import com.wutsi.koki.RefDataFixtures.neighborhoods
import com.wutsi.koki.RoomFixtures.room
import com.wutsi.koki.TenantFixtures.tenants
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.refdata.dto.GetLocationResponse
import com.wutsi.koki.room.dto.CreateRoomRequest
import com.wutsi.koki.room.dto.CreateRoomResponse
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseTerm
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.text.SimpleDateFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateRoomControllerTest : AbstractPageControllerTest() {
    @Test
    fun `create short term`() {
        navigateTo("/rooms/create")
        assertCurrentPageIs(PageName.ROOM_CREATE)

        select2("#accountId", accounts[0].name)
        select("#type", 2)
        select("#leaseType", 1)
        select("#furnishedType", 2)
        select("#numberOfRooms", 3)
        select("#numberOfBathrooms", 4)
        scroll(.25)
        select("#numberOfBeds", 5)
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

        val request = argumentCaptor<CreateRoomRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/rooms"), request.capture(), eq(CreateRoomResponse::class.java)
        )
        assertEquals(accounts[0].id, request.firstValue.accountId)
        assertEquals(RoomType.APARTMENT, request.firstValue.type)
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
        assertEquals(null, request.firstValue.latitude)
        assertEquals(null, request.firstValue.longitude)
        assertEquals(null, request.firstValue.leaseTermDuration)
        assertEquals(null, request.firstValue.dateOfAvailability)
        assertEquals(1909, request.firstValue.yearOfConstruction)

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `create - with full_access permission`() {
        setUpUserWithFullAccessPermissions("room")

        navigateTo("/rooms/create")
        assertCurrentPageIs(PageName.ROOM_CREATE)
    }

    @Test
    fun `create long term`() {
        navigateTo("/rooms/create")
        assertCurrentPageIs(PageName.ROOM_CREATE)

        navigateTo("/rooms/create")
        select2("#accountId", accounts[0].name)
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
        input("#visitFees", "5")
        assertElementHasAttribute("#checkinTime", "disabled")
        assertElementHasAttribute("#checkinTime", "disabled")
        scroll(.25)
        select("#leaseTerm", 1)
        select("#leaseTermDuration", 11)
        select("#yearOfConstruction", 10)
        scrollToBottom()
        select("#country", 3)
        select2("#cityId", "${locations[2].name}, ${locations[0].name}")
        select2("#neighborhoodId", "${neighborhoods[0].name}, ${cities[0].name}")
        input("#street", "340 Nicolet")
        input("#postalCode", "HzH zHz")
        click("button[type=submit]")

        val request = argumentCaptor<CreateRoomRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/rooms"), request.capture(), eq(CreateRoomResponse::class.java)
        )
        assertEquals(accounts[0].id, request.firstValue.accountId)
        assertEquals(RoomType.APARTMENT, request.firstValue.type)
        assertEquals(LeaseType.LONG_TERM, request.firstValue.leaseType)
        assertEquals(LeaseTerm.WEEKLY, request.firstValue.leaseTerm)
        assertEquals(FurnishedType.SEMI_FURNISHED, request.firstValue.furnishedType)
        assertEquals(4, request.firstValue.numberOfRooms)
        assertEquals(5, request.firstValue.numberOfBathrooms)
        assertEquals(5, request.firstValue.numberOfBeds)
        assertEquals(6, request.firstValue.maxGuests)
        assertEquals(null, request.firstValue.pricePerNight)
        assertEquals(500.0, request.firstValue.pricePerMonth)
        assertEquals(5.0, request.firstValue.visitFees)
        assertEquals(tenants[0].currency, request.firstValue.currency)
        assertEquals(locations[2].id, request.firstValue.cityId)
        assertEquals(neighborhoods[0].id, request.firstValue.neighborhoodId)
        assertEquals("340 Nicolet", request.firstValue.street)
        assertEquals("HzH zHz", request.firstValue.postalCode)
        assertEquals(null, request.firstValue.checkinTime)
        assertEquals(null, request.firstValue.checkoutTime)
        assertEquals(null, request.firstValue.latitude)
        assertEquals(null, request.firstValue.longitude)
        assertEquals(11, request.firstValue.leaseTermDuration)
        assertEquals(null, request.firstValue.dateOfAvailability)
        assertEquals(1909, request.firstValue.yearOfConstruction)

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(), any<CreateRoomRequest>(), eq(CreateRoomResponse::class.java)
        )

        navigateTo("/rooms/create")
        select2("#accountId", accounts[0].name)
        select("#type", 2)
        select("#leaseType", 2)
        select("#furnishedType", 2)
        select("#numberOfRooms", 3)
        select("#numberOfBathrooms", 4)
        select("#numberOfBeds", 5)
        scroll(.25)
        select("#maxGuests", 6)
        input("#area", "1500")
        input("#pricePerMonth", "500")
        input("#visitFees", "5")
        scroll(.25)
        select("#leaseTerm", 1)
        select("#leaseTermDuration", 2)
        select("#yearOfConstruction", 10)
        scrollToBottom()
        select("#country", 3)
        select2("#cityId", "${locations[2].name}, ${locations[0].name}")
        select2("#neighborhoodId", "${neighborhoods[0].name}, ${cities[0].name}")
        input("#street", "340 Nicolet")
        input("#postalCode", "HzH zHz")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.ROOM_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun cancel() {
        navigateTo("/rooms/create")
        select2("#accountId", accounts[0].name)
        select("#type", 2)
        select("#leaseType", 2)
        select("#furnishedType", 2)
        select("#numberOfRooms", 3)
        select("#numberOfBathrooms", 4)
        select("#numberOfBeds", 5)
        scroll(.25)
        select("#maxGuests", 6)
        input("#area", "1500")
        input("#pricePerMonth", "500")
        input("#visitFees", "5")
        scroll(.25)
        select("#leaseTerm", 1)
        select("#leaseTermDuration", 2)
        select("#yearOfConstruction", 10)
        scrollToBottom()
        select("#country", 3)
        select2("#cityId", "${locations[2].name}, ${locations[0].name}")
        select2("#neighborhoodId", "${neighborhoods[0].name}, ${cities[0].name}")
        input("#street", "340 Nicolet")
        input("#postalCode", "HzH zHz")
        click(".btn-cancel")

        assertCurrentPageIs(PageName.ROOM_LIST)
    }

    @Test
    fun `create - without permission room-manage`() {
        setUpUserWithoutPermissions(listOf("room:manage"))

        navigateTo("/rooms/create")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/rooms/create")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun clone() {
        doReturn(
            ResponseEntity(
                GetLocationResponse(neighborhoods[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                "$sdkBaseUrl/v1/locations/${room.neighborhoodId}",
                GetLocationResponse::class.java
            )

        navigateTo("/rooms/create?copy-id=${room.id}")

        assertCurrentPageIs(PageName.ROOM_CREATE)
        assertSelectValue("#accountId", room.accountId.toString())
        assertSelectValue("#type", room.type.name)
        assertSelectValue("#leaseType", room.leaseType.name)
        assertSelectValue("#furnishedType", room.furnishedType.name)
        assertSelectValue("#numberOfRooms", room.numberOfRooms.toString())
        assertSelectValue("#numberOfBathrooms", room.numberOfBathrooms.toString())
        assertSelectValue("#numberOfBeds", room.numberOfBeds.toString())
        assertSelectValue("#maxGuests", room.maxGuests.toString())
        assertElementAttribute("#area", "value", room.area.toString())
        assertElementAttribute("#pricePerMonth", "value", room.pricePerMonth?.amount?.toString())
        assertElementAttribute("#pricePerNight", "value", room.pricePerNight?.amount?.toString())
        assertElementHasAttribute("#country option[value=${room.address?.country}]", "selected")
        assertSelectValue("#neighborhoodId", room.neighborhoodId.toString())
        assertElementAttribute("#street", "value", room.address?.street)
        assertElementAttribute("#postalCode", "value", room.address?.postalCode)
        assertElementAttribute("#latitude", "value", room.latitude?.toString())
        assertElementAttribute("#longitude", "value", room.longitude?.toString())
        assertElementAttribute("#visitFees", "value", room.visitFees?.amount?.toString())
        assertSelectValue("#leaseTerm", room.leaseTerm.toString())
        assertSelectValue("#leaseTermDuration", room.leaseTermDuration.toString())
        assertSelectValue("#advanceRent", room.advanceRent.toString())
        assertSelectValue("#yearOfConstruction", room.yearOfConstruction.toString())
        assertElementAttribute(
            "#dateOfAvailability",
            "value",
            SimpleDateFormat("yyyy-MM-dd").format(room.dateOfAvailability)
        )

        scrollToBottom()
        click("button[type=submit]")

        val request = argumentCaptor<CreateRoomRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/rooms"), request.capture(), eq(CreateRoomResponse::class.java)
        )
        assertEquals(room.latitude, request.firstValue.latitude)
        assertEquals(room.longitude, request.firstValue.longitude)
    }
}
