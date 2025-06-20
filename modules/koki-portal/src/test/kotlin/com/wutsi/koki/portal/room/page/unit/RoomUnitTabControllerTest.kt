package com.wutsi.koki.portal.room.page.unit

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.RoomFixtures.room
import com.wutsi.koki.RoomFixtures.roomUnits
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.room.dto.CreateRoomUnitRequest
import com.wutsi.koki.room.dto.CreateRoomUnitResponse
import com.wutsi.koki.room.dto.RoomUnitStatus
import com.wutsi.koki.room.dto.RoomUnitSummary
import com.wutsi.koki.room.dto.SearchRoomUnitResponse
import com.wutsi.koki.room.dto.UpdateRoomUnitRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class RoomUnitTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")

        assertElementCount("tr.room-unit", roomUnits.size)
        assertElementPresent(".btn-create")
        assertElementPresent(".btn-edit")
        assertElementPresent(".btn-refresh")
    }

    @Test
    fun `load more`() {
        var entries = mutableListOf<RoomUnitSummary>()
        repeat(20) {
            entries.add(roomUnits[0].copy())
        }

        doReturn(
            ResponseEntity(
                SearchRoomUnitResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchRoomUnitResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchRoomUnitResponse(roomUnits),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                anyOrNull<String>(),
                eq(SearchRoomUnitResponse::class.java)
            )

        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")
        assertElementCount("tr.room-unit", entries.size)

        scrollToBottom()
        click("#room-unit-load-more button")
        assertElementCount("tr.room-unit", 2 * entries.size)

        scrollToBottom()
        click("#room-unit-load-more button")
        assertElementCount("tr.room-unit", 2 * entries.size + files.size)
    }

    @Test
    fun `read only`() {
        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true&read-only=true")

        assertElementCount("tr.room-unit", roomUnits.size)
        assertElementNotPresent(".btn-create")
        assertElementPresent(".btn-refresh")
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-delete")
    }

    @Test
    fun `list - without permission room-unit-manage`() {
        setupUserWithoutPermissions(listOf("room-unit:manage"))

        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")
        assertElementNotPresent(".btn-create")
        assertElementPresent(".btn-refresh")
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-delete")
    }

    @Test
    fun `list - without permission room-unit`() {
        setupUserWithoutPermissions(listOf("room-unit"))

        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun create() {
        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")

        click(".btn-create")
        assertElementVisible("#koki-modal")
        input("#number", "333")
        select("#floor", 3)
        select("#status", 1)
        click("#btn-room-unit-submit")

        val request = argumentCaptor<CreateRoomUnitRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/room-units"),
            request.capture(),
            eq(CreateRoomUnitResponse::class.java),
        )
        assertEquals(room.id, request.firstValue.roomId)
        assertEquals("333", request.firstValue.number)
        assertEquals(3, request.firstValue.floor)
        assertEquals(RoomUnitStatus.UNDER_MAINTENANCE, request.firstValue.status)

        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `create error`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(), any<CreateRoomUnitRequest>(), eq(CreateRoomUnitResponse::class.java)
        )

        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")

        click(".btn-create")
        assertElementVisible("#koki-modal")
        input("#number", "333")
        select("#floor", 3)
        select("#status", 1)
        click("#btn-room-unit-submit")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        assertElementVisible("#koki-modal")
    }

    @Test
    fun edit() {
        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")

        click("#room-unit-${roomUnits[0].id} .btn-edit")
        assertElementVisible("#koki-modal")
        input("#number", "333")
        select("#floor", 3)
        select("#status", 1)
        click("#btn-room-unit-submit")

        val request = argumentCaptor<UpdateRoomUnitRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/room-units/${roomUnits[0].id}"),
            request.capture(),
            eq(Any::class.java),
        )
        assertEquals("333", request.firstValue.number)
        assertEquals(3, request.firstValue.floor)
        assertEquals(RoomUnitStatus.UNDER_MAINTENANCE, request.firstValue.status)

        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `edit error`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(), any<UpdateRoomUnitRequest>(), eq(Any::class.java)
        )

        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")

        click("#room-unit-${roomUnits[0].id} .btn-edit")
        assertElementVisible("#koki-modal")
        input("#number", "333")
        select("#floor", 3)
        select("#status", 1)
        click("#btn-room-unit-submit")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        assertElementVisible("#koki-modal")
    }

    @Test
    fun delete() {
        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")
        click("#room-unit-${roomUnits[0].id} .btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        verify(rest).delete("$sdkBaseUrl/v1/room-units/${roomUnits[0].id}")
    }

    @Test
    fun `delete cancel`() {
        navigateTo("/room-units/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")
        click("#room-unit-${roomUnits[0].id} .btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(rest, never()).delete(any<String>())
    }
}
