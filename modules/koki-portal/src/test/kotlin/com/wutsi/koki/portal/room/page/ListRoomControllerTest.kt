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

class ListRoomControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/rooms")

        assertCurrentPageIs(PageName.ROOM_LIST)
        assertElementCount("tr.room", rooms.size)
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

        navigateTo("/rooms")

        assertCurrentPageIs(PageName.ROOM_LIST)
        assertElementCount("tr.room", entries.size)

        scrollToBottom()
        click("#room-load-more button", 1000)
        assertElementCount("tr.room", 2 * entries.size)

        scrollToBottom()
        click("#room-load-more button", 1000)
        assertElementCount("tr.room", 2 * entries.size + rooms.size)
    }

    @Test
    fun show() {
        navigateTo("/rooms")
        click("tr.room td a")

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun create() {
        navigateTo("/rooms")
        click(".btn-create")

        assertCurrentPageIs(PageName.ROOM_CREATE)
    }

    @Test
    fun `list - without permission room`() {
        setUpUserWithoutPermissions(listOf("room"))

        navigateTo("/rooms")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `list - without permission room-manage`() {
        setUpUserWithoutPermissions(listOf("room:manage"))

        navigateTo("/rooms")

        assertCurrentPageIs(PageName.ROOM_LIST)
        assertElementNotPresent(".btn-create")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/rooms")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
