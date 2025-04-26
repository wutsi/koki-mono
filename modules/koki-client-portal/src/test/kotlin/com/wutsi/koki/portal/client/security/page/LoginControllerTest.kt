package com.wutsi.koki.portal.client.security.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.common.page.PageName
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.LoginRequest
import com.wutsi.koki.security.dto.LoginResponse
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class
LoginControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            ResponseEntity(
                LoginResponse(accessToken),
                HttpStatus.OK,
            )
        ).whenever(restForAuthentication)
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
        input("#username", "ray.sponsible")
        input("#password", "secret")
        click("BUTTON")

        verify(restForAuthentication).postForEntity(
            "$sdkBaseUrl/v1/auth/login",
            LoginRequest(
                username = "ray.sponsible",
                password = "secret",
                application = ApplicationName.CLIENT,
            ),
            LoginResponse::class.java
        )

        verify(accessTokenHolder).set(accessToken)

        assertCurrentPageIs(PageName.HOME)
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun `login failure`() {
        // GIVEN
        val ex = createHttpClientErrorException(409, ErrorCode.AUTHENTICATION_FAILED)
        doThrow(ex).whenever(restForAuthentication).postForEntity(
            eq("$sdkBaseUrl/v1/auth/login"),
            any<LoginRequest>(),
            eq(LoginResponse::class.java)
        )

        // WHEN
        navigateTo("/login")

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
        input("#username", "ray.sponsible")
        input("#password", "secret")
        click("BUTTON")

        verify(restForAuthentication).postForEntity(
            "$sdkBaseUrl/v1/auth/login",
            LoginRequest(
                username = "ray.sponsible",
                password = "secret",
                application = ApplicationName.CLIENT,
            ),
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
        navigateTo("/invoices")
        setupUserModule()

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
        input("#username", "ray.sponsible")
        input("#password", "secret")
        click("BUTTON")

        verify(restForAuthentication).postForEntity(
            "$sdkBaseUrl/v1/auth/login",
            LoginRequest(
                username = "ray.sponsible",
                password = "secret",
                application = ApplicationName.CLIENT,
            ),
            LoginResponse::class.java
        )

        verify(accessTokenHolder).set(accessToken)

        assertCurrentPageIs(PageName.INVOICE_LIST)
        assertElementNotPresent(".alert-danger")
    }
}
