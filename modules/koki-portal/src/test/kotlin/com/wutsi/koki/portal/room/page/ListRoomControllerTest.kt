package com.wutsi.koki.portal.room.page

import com.wutsi.koki.LodgingFixtures.rooms
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class ListRoomControllerTes : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/products")

        assertCurrentPageIs(PageName.ROOM_LIST)
        assertElementCount("tr.rooms", rooms.size)
    }
}
