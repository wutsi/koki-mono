package com.wutsi.koki.portal.page.auth

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.party.dto.LoginResponse
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(LoginResponse(accessToken)).whenever(kokiAuthentication).login(any(), any())
    }

    @Test
    fun login() {
        // WHEN
        navigateTo("/login")

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
        input("INPUT[name=email]", "ray.sponsible@gmail.com")
        input("INPUT[name=password]", "secret")
        click("BUTTON")

        verify(kokiAuthentication).login("ray.sponsible@gmail.com", "secret")

        val accessTokenArg = argumentCaptor<String>()
        verify(accessTokenHolder).set(accessTokenArg.capture(), any(), any())
        assertEquals(accessToken, accessTokenArg.firstValue)

        assertCurrentPageIs(PageName.HOME)
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun `login failure`() {
        // GIVEN
        val ex = createHttpClientErrorException(409, ErrorCode.AUTHENTICATION_FAILED)
        doThrow(ex).whenever(kokiAuthentication).login(any(), any())

        // WHEN
        navigateTo("/login")

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
        input("INPUT[name=email]", "ray.sponsible@gmail.com")
        input("INPUT[name=password]", "secret")
        click("BUTTON")

        verify(kokiAuthentication).login("ray.sponsible@gmail.com", "secret")

        assertCurrentPageIs(PageName.LOGIN)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `redirect after login`() {
        // GIVEN
        setUpAnonymousUser()

        val form = Form(
            id = "309302",
            name = "FRM-001",
            title = "Incident Report",
        )
        doReturn(GetFormResponse(form)).whenever(kokiForms).form(any())

        // WHEN
        navigateTo("/forms/4304309")
        setupTenantModule()

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
        input("INPUT[name=email]", "ray.sponsible@gmail.com")
        input("INPUT[name=password]", "secret")
        click("BUTTON", 1000)

        verify(kokiAuthentication).login("ray.sponsible@gmail.com", "secret")

        val accessTokenArg = argumentCaptor<String>()
        verify(accessTokenHolder).set(accessTokenArg.capture(), any(), any())
        assertEquals(accessToken, accessTokenArg.firstValue)

        assertCurrentPageIs(PageName.FORM)
    }
}
