package com.wutsi.koki.portal.client.account.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.account.dto.CreateUserRequest
import com.wutsi.koki.account.dto.CreateUserResponse
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.AccountFixtures.account
import com.wutsi.koki.portal.client.AccountFixtures.invitation
import com.wutsi.koki.portal.client.UserFixtures.user
import com.wutsi.koki.portal.client.common.page.PageName
import com.wutsi.koki.tenant.dto.UserStatus
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class InvitationControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        setUpAnonymousUser()

        doReturn(
            ResponseEntity(
                GetAccountResponse(account.copy(userId = null)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetAccountResponse::class.java)
            )
    }

    @Test
    fun show() {
        navigateTo("/invitations/${invitation.id}")

        assertCurrentPageIs(PageName.INVITATION)

        assertElementPresent("#invitation-form")
        assertElementNotPresent("#already-invited")

        input("#email", account.email ?: "")
        input("#username", "ray.sponsible")
        input("#password", "!Qwerty123")
        input("#confirm", "!Qwerty123")
        click("#btn-submit")

        val request = argumentCaptor<CreateUserRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/accounts/${invitation.accountId}/user"),
            request.capture(),
            eq(CreateUserResponse::class.java)
        )
        assertEquals("ray.sponsible", request.firstValue.username)
        assertEquals("!Qwerty123", request.firstValue.password)
        assertEquals(UserStatus.ACTIVE, request.firstValue.status)

        click("#btn-login")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `invalid email`() {
        navigateTo("/invitations/${invitation.id}")

        assertCurrentPageIs(PageName.INVITATION)

        input("#email", "xxx@gmail.com")
        input("#username", "ray.sponsible")
        input("#password", "!Qwerty123")
        input("#confirm", "!Qwerty12345678")
        click("#btn-submit")

        verify(rest, never()).postForEntity(
            any<String>(),
            any<CreateUserRequest>(),
            eq(CreateUserResponse::class.java)
        )

        assertCurrentPageIs(PageName.INVITATION)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `password mismatch`() {
        navigateTo("/invitations/${invitation.id}")

        assertCurrentPageIs(PageName.INVITATION)

        input("#email", account.email ?: "")
        input("#username", "ray.sponsible")
        input("#password", "!Qwerty123")
        input("#confirm", "!Qwerty12345678")
        click("#btn-submit")

        verify(rest, never()).postForEntity(
            any<String>(),
            any<CreateUserRequest>(),
            eq(CreateUserResponse::class.java)
        )

        assertCurrentPageIs(PageName.INVITATION)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `weak password`() {
        navigateTo("/invitations/${invitation.id}")

        assertCurrentPageIs(PageName.INVITATION)

        input("#email", account.email ?: "")
        input("#username", "ray.sponsible")
        input("#password", "aaQwerty123")
        input("#confirm", "aaQwerty123")
        click("#btn-submit")

        verify(rest, never()).postForEntity(
            any<String>(),
            any<CreateUserRequest>(),
            eq(CreateUserResponse::class.java)
        )
    }

    @Test
    fun `already invited`() {
        doReturn(
            ResponseEntity(
                GetAccountResponse(account.copy(accountTypeId = user.id)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetAccountResponse::class.java)
            )

        navigateTo("/invitations/${invitation.id}")

        assertCurrentPageIs(PageName.INVITATION)

        assertElementNotPresent("#invitation-form")
        assertElementPresent("#already-invited")

        click("#btn-login")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
