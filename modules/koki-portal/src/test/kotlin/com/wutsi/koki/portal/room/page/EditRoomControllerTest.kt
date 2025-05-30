package com.wutsi.koki.portal.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
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
import com.wutsi.koki.room.dto.LeaseTerm
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.UpdateRoomRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class EditRoomControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/rooms/${room.id}/edit")
        assertCurrentPageIs(PageName.ROOM_EDIT)

        input("#title", "Premium Room")
        select("#type", 2)
        select("#leaseType", 1)
        select("#furnishedType", 2)
        select("#numberOfRooms", 3)
        select("#numberOfBathrooms", 4)
        select("#numberOfBeds", 5)
        scroll(.25)
        select("#maxGuests", 6)
        input("#area", "1500")
        input("#pricePerNight", "75")
        input("#pricePerMonth", "500")
        select("#checkinTime", 14)
        select("#checkoutTime", 10)
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
        assertEquals(RoomType.HOUSE, request.firstValue.type)
        assertEquals(LeaseType.SHORT_TERM, request.firstValue.leaseType)
        assertEquals(LeaseTerm.UNKNOWN, request.firstValue.leaseTerm)
        assertEquals(FurnishedType.SEMI_FURNISHED, request.firstValue.furnishedType)
        assertEquals(4, request.firstValue.numberOfRooms)
        assertEquals(5, request.firstValue.numberOfBathrooms)
        assertEquals(6, request.firstValue.numberOfBeds)
        assertEquals(7, request.firstValue.maxGuests)
        assertEquals(75.0, request.firstValue.pricePerNight)
        assertEquals(500.0, request.firstValue.pricePerMonth)
        assertEquals(tenants[0].currency, request.firstValue.currency)
        assertEquals(locations[2].id, request.firstValue.cityId)
        assertEquals(neighborhoods[0].id, request.firstValue.neighborhoodId)
        assertEquals("340 Nicolet", request.firstValue.street)
        assertEquals("HzH zHz", request.firstValue.postalCode)
        assertEquals("13:00", request.firstValue.checkinTime)
        assertEquals("09:00", request.firstValue.checkoutTime)
        assertEquals("This is a description", request.firstValue.description)

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(), any<UpdateRoomRequest>(), eq(Any::class.java)
        )

        navigateTo("/rooms/${room.id}/edit")
        select("#type", 2)
        select("#leaseType", 1)
        select("#furnishedType", 2)
        select("#numberOfRooms", 3)
        select("#numberOfBathrooms", 4)
        select("#numberOfBeds", 5)
        scroll(.25)
        select("#maxGuests", 6)
        input("#area", "1500")
        input("#pricePerNight", "75")
        input("#pricePerMonth", "500")
        select("#checkinTime", 14)
        select("#checkoutTime", 10)
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
        select("#leaseType", 1)
        select("#furnishedType", 2)
        select("#numberOfRooms", 3)
        select("#numberOfBathrooms", 4)
        select("#numberOfBeds", 5)
        scroll(.25)
        select("#maxGuests", 6)
        input("#area", "1500")
        input("#pricePerNight", "75")
        input("#pricePerMonth", "500")
        select("#checkinTime", 14)
        select("#checkoutTime", 10)
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
        setUpUserWithoutPermissions(listOf("room:manage"))

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
