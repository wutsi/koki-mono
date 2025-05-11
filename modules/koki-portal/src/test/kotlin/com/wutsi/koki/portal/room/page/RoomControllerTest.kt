package com.wutsi.koki.portal.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.RoomFixtures.room
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class RoomControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun delete() {
        navigateTo("/rooms/${room.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `delete - dismiss`() {
        navigateTo("/rooms/${room.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(rest, never()).delete(any<String>())
        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `delete - error`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).delete(any<String>())

        navigateTo("/rooms/${room.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.ROOM)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun edit() {
        navigateTo("/rooms/${room.id}")
        click(".btn-edit")

        assertCurrentPageIs(PageName.ROOM_EDIT)
    }

    @Test
    fun `show - without permission room`() {
        setUpUserWithoutPermissions(listOf("room"))

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `show - without permission room-manage`() {
        setUpUserWithoutPermissions(listOf("room:manage"))

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM)
        assertElementNotPresent(".btn-edit")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
