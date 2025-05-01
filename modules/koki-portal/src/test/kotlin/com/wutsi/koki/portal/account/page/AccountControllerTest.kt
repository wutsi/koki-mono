package com.wutsi.koki.portal.account.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AccountFixtures.account
import com.wutsi.koki.AccountFixtures.invitation
import com.wutsi.koki.ContactFixtures
import com.wutsi.koki.EmailFixtures
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.InvoiceFixtures
import com.wutsi.koki.NoteFixtures
import com.wutsi.koki.TaxFixtures
import com.wutsi.koki.account.dto.CreateInvitationRequest
import com.wutsi.koki.account.dto.CreateInvitationResponse
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class AccountControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/accounts/${account.id}")

        assertCurrentPageIs(PageName.ACCOUNT)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/accounts/${account.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/accounts/${account.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/accounts/${account.id}")
        assertCurrentPageIs(PageName.ACCOUNT_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `delete - dismiss`() {
        navigateTo("/accounts/${account.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(rest, never()).delete(any<String>())
        assertCurrentPageIs(PageName.ACCOUNT)
    }

    @Test
    fun `delete - error`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).delete(any<String>())

        navigateTo("/accounts/${account.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.ACCOUNT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun edit() {
        navigateTo("/accounts/${account.id}")
        click(".btn-edit")
        assertCurrentPageIs(PageName.ACCOUNT_EDIT)
    }

    @Test
    fun contacts() {
        navigateTo("/accounts/${account.id}?tab=contact")

        Thread.sleep(1000)
        assertElementCount(".tab-contacts tr.contact", ContactFixtures.contacts.size)

        click(".btn-add-contact")
        assertCurrentPageIs(PageName.CONTACT_CREATE)
    }

    @Test
    fun files() {
        navigateTo("/accounts/${account.id}?tab=file")

        Thread.sleep(1000)
        assertElementCount(".tab-files .file", FileFixtures.files.size)
    }

    @Test
    fun invoices() {
        navigateTo("/accounts/${account.id}?tab=invoice")

        Thread.sleep(1000)
        assertElementCount(".tab-invoices tr.invoice", InvoiceFixtures.invoices.size)
    }

    @Test
    fun notes() {
        navigateTo("/accounts/${account.id}?tab=note")

        Thread.sleep(1000)
        assertElementCount(".tab-notes .note", NoteFixtures.notes.size)
    }

    @Test
    fun taxes() {
        navigateTo("/accounts/${account.id}?tab=tax")

        Thread.sleep(1000)
        assertElementCount(".tab-taxes .tax", TaxFixtures.taxes.size)
    }

    @Test
    fun emails() {
        navigateTo("/accounts/${account.id}?tab=email")

        Thread.sleep(1000)
        assertElementCount(".tab-emails .email", EmailFixtures.emails.size)
    }

    @Test
    fun user() {
        navigateTo("/accounts/${account.id}?tab=user")

        Thread.sleep(1000)
        assertElementPresent(".user")
    }

    @Test
    fun `user - no invitation`() {
        doReturn(
            ResponseEntity(
                GetAccountResponse(account.copy(userId = null, invitationId = null)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetAccountResponse::class.java)
            )

        navigateTo("/accounts/${account.id}?tab=user")

        Thread.sleep(1000)
        assertElementPresent("#invitation-none")

        click(".btn-invite")

        verify(rest).postForEntity(
            "$sdkBaseUrl/v1/invitations",
            CreateInvitationRequest(account.id),
            CreateInvitationResponse::class.java
        )
    }

    @Test
    fun `user - with invitation`() {
        doReturn(
            ResponseEntity(
                GetAccountResponse(account.copy(userId = null, invitationId = invitation.id)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetAccountResponse::class.java)
            )

        navigateTo("/accounts/${account.id}?tab=user")

        Thread.sleep(1000)
        assertElementPresent("#invitation-${invitation.id}")

        click(".btn-invite")

        verify(rest).postForEntity(
            "$sdkBaseUrl/v1/invitations",
            CreateInvitationRequest(account.id),
            CreateInvitationResponse::class.java
        )
    }

    @Test
    fun `show - without permission account`() {
        setUpUserWithoutPermissions(listOf("account"))

        navigateTo("/accounts/${account.id}")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `show - without permission account-manage`() {
        setUpUserWithoutPermissions(listOf("account:manage"))

        navigateTo("/accounts/${account.id}")

        assertCurrentPageIs(PageName.ACCOUNT)
        assertElementNotPresent(".account-summary .btn-edit")
    }

    @Test
    fun `show - without permission account-delete`() {
        setUpUserWithoutPermissions(listOf("account:delete"))

        navigateTo("/accounts/${account.id}")

        assertCurrentPageIs(PageName.ACCOUNT)
        assertElementNotPresent(".account-summary .btn-delete")
    }

    @Test
    fun `delete - without permission account-delete`() {
        setUpUserWithoutPermissions(listOf("account:delete"))

        navigateTo("/accounts/${account.id}/delete")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
