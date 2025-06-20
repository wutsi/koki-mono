package com.wutsi.koki.portal.room.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.RoomFixtures.room
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.room.dto.SaveRoomGeoLocationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomMapControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/rooms/${room.id}/map")
        assertCurrentPageIs(PageName.ROOM_MAP)

        click("button[type=submit]")

        val request = argumentCaptor<SaveRoomGeoLocationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/rooms/${room.id}/geolocation"),
            request.capture(),
            eq(Any::class.java),
        )

        assertEquals(room.latitude, request.firstValue.latitude)
        assertEquals(room.longitude, request.firstValue.longitude)
        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun back() {
        navigateTo("/rooms/${room.id}/map")

        click(".btn-back")
        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `open in google-map`() {
        navigateTo("/rooms/${room.id}/map")

        Thread.sleep(2000) // Wait for the map
        click("#btn-google-map", 2000)

        val handles = driver.getWindowHandles().toList()
        assertEquals(2, handles.size)
        driver.switchTo().window(handles[1])

        assertEquals("https://www.google.com/maps?t=m&z=17&q=loc:${room.latitude}+${room.longitude}", driver.currentUrl)
    }

    @Test
    fun `change lat-long`() {
        navigateTo("/rooms/${room.id}/map")

        Thread.sleep(2000) // Wait for the map to render
        click("#btn-lat-long")

        val alert = driver.switchTo().alert()
        alert.sendKeys("3.8983324456062647, 11.507542553587328")
        alert.accept()

        click("button[type=submit]")

        val request = argumentCaptor<SaveRoomGeoLocationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/rooms/${room.id}/geolocation"),
            request.capture(),
            eq(Any::class.java),
        )

        assertEquals(3.8983324456062647, request.firstValue.latitude)
        assertEquals(11.507542553587328, request.firstValue.longitude)
        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `show - without permission room`() {
        setupUserWithoutPermissions(listOf("room"))

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `show - without permission room-manage`() {
        setupUserWithoutPermissions(listOf("room:manage"))

        navigateTo("/rooms/${room.id}/map")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/rooms/${room.id}/map")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
