package com.wutsi.koki.portal.page.settings.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ServiceFixtures.service
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.service.dto.AuthorizationType
import com.wutsi.koki.service.dto.GetServiceResponse
import kotlin.test.Test

class ShowServiceControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/services/${service.id}")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE)

        assertElementNotPresent(".label-danger")
        assertElementPresent("#username")
        assertElementPresent("#password")
        assertElementNotPresent("#api-key")
    }

    @Test
    fun `show api-key authorization`() {
        val srv = service.copy(authorizationType = AuthorizationType.API_KEY)
        doReturn(GetServiceResponse(srv)).whenever(kokiServices).service(any())

        navigateTo("/settings/services/${service.id}")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE)

        assertElementNotPresent(".label-danger")
        assertElementNotPresent("#username")
        assertElementNotPresent("#password")
        assertElementPresent("#api-key")
    }

    @Test
    fun `show no  authorization`() {
        val srv = service.copy(authorizationType = AuthorizationType.NONE)
        doReturn(GetServiceResponse(srv)).whenever(kokiServices).service(any())

        navigateTo("/settings/services/${service.id}")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE)

        assertElementNotPresent(".label-danger")
        assertElementNotPresent("#username")
        assertElementNotPresent("#password")
        assertElementNotPresent("#api-key")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/services/${service.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/settings/services/${service.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiServices).delete(service.id)
        assertCurrentPageIs(PageName.SETTINGS_SERVICE_DELETED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE_LIST)
    }

    @Test
    fun `delete dismiss`() {
        navigateTo("/settings/services/${service.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        assertCurrentPageIs(PageName.SETTINGS_SERVICE)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.SERVICE_IN_USE)
        doThrow(ex).whenever(kokiServices).delete(any())

        navigateTo("/settings/services/${service.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        assertCurrentPageIs(PageName.SETTINGS_SERVICE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun edit() {
        navigateTo("/settings/services/${service.id}")
        click(".btn-edit")

        assertCurrentPageIs(PageName.SETTINGS_SERVICE_EDIT)
    }
}
