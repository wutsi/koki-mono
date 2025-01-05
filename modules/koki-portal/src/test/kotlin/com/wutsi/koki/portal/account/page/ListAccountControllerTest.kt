package com.wutsi.koki.portal.account.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures.accounts
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.account.dto.SearchAccountResponse
import com.wutsi.koki.portal.page.PageName
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
        doReturn(SearchAccountResponse(entries))
            .doReturn(SearchAccountResponse(accounts))
            .whenever(kokiAccounts)
            .accounts(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/accounts")

        assertCurrentPageIs(PageName.ACCOUNT_LIST)
        assertElementCount("tr.account", entries.size)

        scrollToBottom()
        click("#account-load-more a", 1000)
        assertElementCount("tr.account", entries.size + accounts.size)
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
    fun edit() {
        navigateTo("/accounts")
        click(".btn-edit")
        assertCurrentPageIs(PageName.ACCOUNT_EDIT)
    }
}
