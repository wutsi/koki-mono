package com.wutsi.koki.portal.account.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AccountFixtures.accounts
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.account.dto.SearchAccountResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListAccountControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/accounts")

        assertCurrentPageIs(PageName.ACCOUNT_LIST)
        assertElementCount("tr.account", accounts.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<AccountSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(accounts[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchAccountResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchAccountResponse::class.java)
            )

        navigateTo("/accounts")

        assertCurrentPageIs(PageName.ACCOUNT_LIST)
        assertElementCount("tr.account", entries.size)

        scrollToBottom()
        click("#account-load-more a", 1000)
        assertElementCount("tr.account", 2 * entries.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/accounts")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun create() {
        navigateTo("/accounts")
        click(".btn-create")
        assertCurrentPageIs(PageName.ACCOUNT_CREATE)
    }

    @Test
    fun show() {
        navigateTo("/accounts")
        click("tr.account a")
        assertCurrentPageIs(PageName.ACCOUNT)
    }

    @Test
    fun `list - without permission account`() {
        setUpUserWithoutPermissions(listOf("account"))

        navigateTo("/accounts")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `list - without permission account-manage`() {
        setUpUserWithoutPermissions(listOf("account:manage"))

        navigateTo("/accounts")

        assertCurrentPageIs(PageName.ACCOUNT_LIST)
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-create")
    }
}
