package com.wutsi.koki.portal.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.RoomFixtures.rooms
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.SearchRoomResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class RoomTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/rooms/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")

        assertElementCount(".tab-rooms tr.room", rooms.size)
        assertElementPresent(".btn-add-room")
    }

    @Test
    fun `list - with full_access permission`() {
        setupUserWithFullAccessPermissions("room")

        navigateTo("/rooms/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")

        assertElementCount(".tab-rooms tr.room", rooms.size)
        assertElementPresent(".btn-add-room")
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<RoomSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(rooms[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchRoomResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchRoomResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchRoomResponse(rooms),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoomResponse::class.java)
            )

        navigateTo("/rooms/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")

        assertElementCount("tr.room", entries.size)

        scrollToBottom()
        click("#room-load-more button", 1000)
        assertElementCount("tr.room", 2 * entries.size)

        scrollToBottom()
        click("#room-load-more button", 1000)
        assertElementCount("tr.room", 2 * entries.size + rooms.size)
    }

    @Test
    fun addNew() {
        navigateTo("/rooms/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")
        click(".btn-add-room")

        assertCurrentPageIs(PageName.ROOM_CREATE)
    }

    @Test
    fun view() {
        navigateTo("/rooms/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")
        click("tr.room a")

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `read only`() {
        navigateTo("/rooms/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true&read-only=true")

        assertElementNotPresent(".btn-add-room")
    }

    @Test
    fun `list - without permission room-manage`() {
        setupUserWithoutPermissions(listOf("room:manage"))

        navigateTo("/rooms/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertElementNotPresent(".btn-add-room")
    }

    @Test
    fun `list - without permission room`() {
        setupUserWithoutPermissions(listOf("room"))

        navigateTo("/rooms/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
