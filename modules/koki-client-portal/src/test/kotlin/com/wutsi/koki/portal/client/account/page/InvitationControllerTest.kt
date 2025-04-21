package com.wutsi.koki.portal.client.account.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.account.dto.CreateAccountUserRequest
import com.wutsi.koki.account.dto.CreateAccountUserResponse
import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.AccountFixtures.account
import com.wutsi.koki.portal.client.AccountFixtures.invitation
import com.wutsi.koki.portal.client.common.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class InvitationControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        setUpAnonymousUser()
    }

    @Test
    fun show() {
        navigateTo("/invitations/${invitation.id}")

        assertCurrentPageIs(PageName.INVITATION)

        input("#email", account.email ?: "")
        input("#username", "ray.sponsible")
        input("#password", "!Qwerty123")
        input("#confirm", "!Qwerty123")
        click("#btn-submit")

        val request = argumentCaptor<CreateAccountUserRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/account-users"),
            request.capture(),
            eq(CreateAccountUserResponse::class.java)
        )
        assertEquals("ray.sponsible", request.firstValue.username)
        assertEquals("!Qwerty123", request.firstValue.password)
        assertEquals(account.id, request.firstValue.accountId)

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
            any<CreateAccountUserRequest>(),
            eq(CreateAccountUserResponse::class.java)
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
            any<CreateAccountUserRequest>(),
            eq(CreateAccountUserResponse::class.java)
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
            any<CreateAccountUserRequest>(),
            eq(CreateAccountUserResponse::class.java)
        )
    }
}
