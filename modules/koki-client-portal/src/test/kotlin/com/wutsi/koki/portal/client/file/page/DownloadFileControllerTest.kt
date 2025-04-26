package com.wutsi.koki.portal.client.file.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.AccountFixtures.account
import com.wutsi.koki.portal.client.FileFixtures.file
import com.wutsi.koki.portal.client.TaxFixtures.tax
import com.wutsi.koki.portal.client.common.page.PageName
import com.wutsi.koki.tax.dto.GetTaxResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class DownloadFileControllerTest : AbstractPageControllerTest() {
    @Test
    fun download() {
        navigateTo("/files/${file.id}/download?owner-id=${tax.id}&owner-type=TAX&test-mode=true")
    }

    @Test
    fun `now owner of associated tax`() {
        doReturn(
            ResponseEntity(
                GetTaxResponse(tax = tax.copy(accountId = 999)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetTaxResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetAccountResponse(account = account.copy(id = 999)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                eq("$sdkBaseUrl/v1/accounts/999"),
                eq(GetAccountResponse::class.java)
            )

        navigateTo("/files/${file.id}/download?owner-id=${tax.id}&owner-type=TAX&test-mode=true")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `file not found`() {
        doReturn(
            ResponseEntity(
                SearchFileResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchFileResponse::class.java)
            )

        navigateTo("/files/${file.id}/download?owner-id=${tax.id}&owner-type=TAX&test-mode=true")
        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `invalid id`() {
        navigateTo("/files/four-hundred/download?owner-id=${tax.id}&owner-type=TAX&test-mode=true")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/files/${file.id}/download?owner-id=${tax.id}&owner-type=TAX&test-mode=true")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
