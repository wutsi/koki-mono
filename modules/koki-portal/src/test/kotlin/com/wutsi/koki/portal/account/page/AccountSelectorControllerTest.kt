package com.wutsi.koki.portal.account.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.UserFixtures.user
import com.wutsi.koki.account.dto.SearchAccountResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AccountSelectorControllerTest : AbstractPageControllerTest() {
    @Test
    fun search() {
        navigateTo("/accounts/selector/search?q=Real&limit=10&offset=1")

        val url = argumentCaptor<String>()
        verify(rest).getForEntity(
            url.capture(),
            eq(SearchAccountResponse::class.java)
        )

        assertEquals(
            "http://localhost:8080/v1/accounts?q=Real&managed-by-id=${user.id}&limit=10&offset=1",
            url.firstValue
        )
    }

    @Test
    fun fullAccess() {
        setupUserWithFullAccessPermissions("account")

        navigateTo("/accounts/selector/search?q=Real")

        val url = argumentCaptor<String>()
        verify(rest).getForEntity(
            url.capture(),
            eq(SearchAccountResponse::class.java)
        )

        assertEquals(
            "http://localhost:8080/v1/accounts?q=Real&limit=20&offset=0",
            url.firstValue
        )
    }
}
