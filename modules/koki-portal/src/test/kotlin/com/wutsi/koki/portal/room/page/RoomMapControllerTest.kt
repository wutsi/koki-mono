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
    fun `show - without permission room`() {
        setUpUserWithoutPermissions(listOf("room"))

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `show - without permission room-manage`() {
        setUpUserWithoutPermissions(listOf("room:manage"))

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
