package com.wutsi.koki.portal.client

import com.wutsi.koki.invoice.dto.Customer
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.InvoiceSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date
import java.util.UUID

object InvoiceFixtures {
    val invoices = listOf(
        InvoiceSummary(
            id = 100L,
            paynowId = UUID.randomUUID().toString(),
            number = 12039209L,
            createdAt = DateUtils.addDays(Date(), -5),
            dueAt = DateUtils.addDays(Date(), 30),
            customer = Customer(accountId = AccountFixtures.accounts[0].id, name = AccountFixtures.accounts[0].name),
            status = InvoiceStatus.DRAFT,
            currency = "CAD",
            amountPaid = 0.0,
            totalAmount = 1000.0,
            amountDue = 1000.0,
        ),
        InvoiceSummary(
            id = 101L,
            paynowId = UUID.randomUUID().toString(),
            number = 12039210L,
            createdAt = DateUtils.addDays(Date(), -4),
            dueAt = DateUtils.addDays(Date(), 30),
            customer = Customer(name = "Roger Milla"),
            status = InvoiceStatus.OPENED,
            currency = "CAD",
            amountPaid = 500.0,
            totalAmount = 750.0,
            amountDue = 250.0,
        ),
        InvoiceSummary(
            id = 102L,
            paynowId = UUID.randomUUID().toString(),
            number = 12039211L,
            createdAt = Date(),
            dueAt = null,
            customer = Customer(name = "John Smith"),
            status = InvoiceStatus.PAID,
            currency = "CAD",
            amountPaid = 500.0,
            totalAmount = 500.0,
            amountDue = 0.0,
        ),
        InvoiceSummary(
            id = 103L,
            paynowId = UUID.randomUUID().toString(),
            number = 12039221L,
            createdAt = Date(),
            dueAt = null,
            customer = Customer(accountId = AccountFixtures.accounts[1].id, name = AccountFixtures.accounts[1].name),
            status = InvoiceStatus.PAID,
            currency = "CAD",
            amountPaid = 500.0,
            totalAmount = 500.0,
            amountDue = 0.0,
        ),
        InvoiceSummary(
            id = 104L,
            paynowId = UUID.randomUUID().toString(),
            number = 12039231L,
            createdAt = Date(),
            dueAt = null,
            customer = Customer(accountId = AccountFixtures.accounts[2].id, name = AccountFixtures.accounts[2].name),
            status = InvoiceStatus.VOIDED,
            currency = "CAD",
            amountPaid = 500.0,
            totalAmount = 500.0,
            amountDue = 0.0,
        ),
    )
}
