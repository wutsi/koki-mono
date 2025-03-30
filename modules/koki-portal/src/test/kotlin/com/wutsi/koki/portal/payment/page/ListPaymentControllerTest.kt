package com.wutsi.koki.portal.payment.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.PaymentFixtures.transactions
import com.wutsi.koki.payment.dto.SearchTransactionResponse
import com.wutsi.koki.payment.dto.TransactionSummary
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID
import kotlin.test.Test

class ListPaymentControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/payments")

        assertCurrentPageIs(PageName.PAYMENT_LIST)
        assertElementCount("tr.payment", transactions.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<TransactionSummary>()
        repeat(20) {
            entries.add(transactions[0].copy(id = UUID.randomUUID().toString()))
        }
        doReturn(
            ResponseEntity(
                SearchTransactionResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                anyOrNull<String>(),
                eq(SearchTransactionResponse::class.java)
            )

        navigateTo("/payments")

        assertCurrentPageIs(PageName.PAYMENT_LIST)
        assertElementCount("tr.payment", entries.size)

        scrollToBottom()
        click("#payment-load-more a", 1000)
        assertElementCount("tr.payment", 2 * entries.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/payments")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `list - without permission payment`() {
        setUpUserWithoutPermissions(listOf("payment"))

        navigateTo("/payments")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `list - without permission payment-manage`() {
        setUpUserWithoutPermissions(listOf("payment:manage"))

        navigateTo("/payments")

        assertCurrentPageIs(PageName.PAYMENT_LIST)
//        assertElementNotPresent(".btn-edit")
//        assertElementNotPresent(".btn-create")
    }

    @Test
    fun show() {
        navigateTo("/payments")
        click("tr.payment a")

        assertCurrentPageIs(PageName.PAYMENT)
    }
}
