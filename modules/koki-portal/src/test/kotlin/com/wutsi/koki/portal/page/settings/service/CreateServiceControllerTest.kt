package com.wutsi.koki.portal.page.settings.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ServiceFixtures.service
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.service.dto.AuthorizationType
import com.wutsi.koki.service.dto.CreateServiceRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateServiceControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/settings/services/create")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE_CREATE)
        assertElementNotPresent(".alert-danger")

        input("input[name=name]", "M-XXX")
        input("input[name=title]", "This is the new subject")
        input("textarea[name=description]", "This is the description")
        input("input[name=baseUrl]", "https://prod.paypal.com")
        select("select[name=authorizationType]", 2)
        assertElementNotVisible(".auth-api-key")
        scrollToBottom()
        input("input[name=username]", "u-paypal")
        input("input[name=password]", "paypal-secret")
        select("select[name=active]", 1)
        click("button[type=submit]")

        val request = argumentCaptor<CreateServiceRequest>()
        verify(kokiServices).create(request.capture())

        assertEquals("M-XXX", request.firstValue.name)
        assertEquals("This is the new subject", request.firstValue.title)
        assertEquals("This is the description", request.firstValue.description)
        assertEquals("https://prod.paypal.com", request.firstValue.baseUrl)
        assertEquals(AuthorizationType.BASIC, request.firstValue.authorizationType)
        assertEquals("u-paypal", request.firstValue.username)
        assertEquals("paypal-secret", request.firstValue.password)
        assertEquals(null, request.firstValue.apiKey)
        assertEquals(false, request.firstValue.active)

        assertCurrentPageIs(PageName.SETTINGS_SERVICE_SAVED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE_LIST)
    }

    @Test
    fun `no authentication`() {
        navigateTo("/settings/services/create")

        input("input[name=name]", "M-XXX")
        input("input[name=title]", "This is the new subject")
        input("textarea[name=description]", "This is the description")
        input("input[name=baseUrl]", "https://prod.paypal.com")
        select("select[name=authorizationType]", 1)
        assertElementNotVisible(".auth-basic")
        assertElementNotVisible(".auth-api-key")
        scrollToBottom()
        select("select[name=active]", 0)
        click("button[type=submit]")

        val request = argumentCaptor<CreateServiceRequest>()
        verify(kokiServices).create(request.capture())

        assertEquals("M-XXX", request.firstValue.name)
        assertEquals("This is the new subject", request.firstValue.title)
        assertEquals("This is the description", request.firstValue.description)
        assertEquals("https://prod.paypal.com", request.firstValue.baseUrl)
        assertEquals(AuthorizationType.NONE, request.firstValue.authorizationType)
        assertEquals(null, request.firstValue.username)
        assertEquals(null, request.firstValue.password)
        assertEquals(null, request.firstValue.apiKey)
        assertEquals(true, request.firstValue.active)

        assertCurrentPageIs(PageName.SETTINGS_SERVICE_SAVED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE_LIST)
    }

    @Test
    fun `api-key authentication`() {
        navigateTo("/settings/services/create")

        input("input[name=name]", "M-XXX")
        input("input[name=title]", "This is the new subject")
        input("textarea[name=description]", "This is the description")
        input("input[name=baseUrl]", "https://prod.paypal.com")
        select("select[name=authorizationType]", 3)
        assertElementNotVisible(".auth-basic")
        input("input[name=apiKey]", "api-key-0000")
        scrollToBottom()
        click("button[type=submit]")

        val request = argumentCaptor<CreateServiceRequest>()
        verify(kokiServices).create(request.capture())

        assertEquals("M-XXX", request.firstValue.name)
        assertEquals("This is the new subject", request.firstValue.title)
        assertEquals("This is the description", request.firstValue.description)
        assertEquals("https://prod.paypal.com", request.firstValue.baseUrl)
        assertEquals(AuthorizationType.API_KEY, request.firstValue.authorizationType)
        assertEquals(null, request.firstValue.username)
        assertEquals(null, request.firstValue.password)
        assertEquals("api-key-0000", request.firstValue.apiKey)
        assertEquals(service.active, request.firstValue.active)

        assertCurrentPageIs(PageName.SETTINGS_SERVICE_SAVED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE_LIST)
    }

    @Test
    fun cancel() {
        navigateTo("/settings/services/create")

        scrollToBottom()
        click(".btn-cancel")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.SERVICE_IN_USE)
        doThrow(ex).whenever(kokiServices).create(any())

        navigateTo("/settings/services/create")

        input("input[name=name]", "M-XXX")
        input("input[name=title]", "This is the new subject")
        input("textarea[name=description]", "This is the description")
        input("input[name=baseUrl]", "https://prod.paypal.com")
        select("select[name=authorizationType]", 3)
        scrollToBottom()
        select("select[name=active]", 1)
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_SERVICE_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/services/create")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
