package com.wutsi.koki.portal.auth.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures.account
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.party.dto.LoginRequest
import com.wutsi.koki.party.dto.LoginResponse
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            ResponseEntity(
                LoginResponse(accessToken),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                eq("$sdkBaseUrl/v1/auth/login"),
                any<LoginRequest>(),
                eq(LoginResponse::class.java)
            )
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

        verify(rest).postForEntity(
            "$sdkBaseUrl/v1/auth/login",
            LoginRequest("ray.sponsible@gmail.com", "secret"),
            LoginResponse::class.java
        )

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
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/auth/login"),
            any<LoginRequest>(),
            eq(LoginResponse::class.java)
        )

        // WHEN
        navigateTo("/login")

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
        input("INPUT[name=email]", "ray.sponsible@gmail.com")
        input("INPUT[name=password]", "secret")
        click("BUTTON")

        verify(rest).postForEntity(
            "$sdkBaseUrl/v1/auth/login",
            LoginRequest("ray.sponsible@gmail.com", "secret"),
            LoginResponse::class.java
        )

        assertCurrentPageIs(PageName.LOGIN)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `redirect after login`() {
        // GIVEN
        setUpAnonymousUser()

        // WHEN
        navigateTo("/accounts/${account.id}")
        setupTenantModule()

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
        input("INPUT[name=email]", "ray.sponsible@gmail.com")
        input("INPUT[name=password]", "secret")
        click("BUTTON", 1000)

        verify(rest).postForEntity(
            "$sdkBaseUrl/v1/auth/login",
            LoginRequest("ray.sponsible@gmail.com", "secret"),
            LoginResponse::class.java
        )

        val accessTokenArg = argumentCaptor<String>()
        verify(accessTokenHolder).set(accessTokenArg.capture(), any(), any())
        assertEquals(accessToken, accessTokenArg.firstValue)

        assertCurrentPageIs(PageName.ACCOUNT)
    }
}
