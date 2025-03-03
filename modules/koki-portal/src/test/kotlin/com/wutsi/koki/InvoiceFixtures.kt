package com.wutsi.koki

import com.wutsi.koki.invoice.dto.Customer
import com.wutsi.koki.invoice.dto.Invoice
import com.wutsi.koki.invoice.dto.InvoiceItem
import com.wutsi.koki.invoice.dto.InvoiceSalesTax
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.InvoiceSummary
import com.wutsi.koki.refdata.dto.Address
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object InvoiceFixtures {
    val invoices = listOf(
        InvoiceSummary(
            id = 100L,
            number = 12039209L,
            createdAt = DateUtils.addDays(Date(), -5),
            dueAt = DateUtils.addDays(Date(), 30),
            customer = Customer(accountId = AccountFixtures.accounts[0].id, name = AccountFixtures.accounts[0].name),
            taxId = TaxFixtures.taxes[0].id,
            status = InvoiceStatus.DRAFT,
            currency = "CAD",
            amountPaid = 0.0,
            totalAmount = 1000.0,
            amountDue = 1000.0,
        ),
        InvoiceSummary(
            id = 101L,
            number = 12039210L,
            createdAt = DateUtils.addDays(Date(), -4),
            dueAt = DateUtils.addDays(Date(), 30),
            customer = Customer(name = "Roger Milla"),
            taxId = TaxFixtures.taxes[1].id,
            status = InvoiceStatus.OPENED,
            currency = "CAD",
            amountPaid = 500.0,
            totalAmount = 750.0,
            amountDue = 250.0,
        ),
        InvoiceSummary(
            id = 102L,
            number = 12039211L,
            createdAt = Date(),
            dueAt = null,
            customer = Customer(name = "John Smith"),
            taxId = TaxFixtures.taxes[2].id,
            status = InvoiceStatus.PAID,
            currency = "CAD",
            amountPaid = 500.0,
            totalAmount = 500.0,
            amountDue = 0.0,
        ),
        InvoiceSummary(
            id = 103L,
            number = 12039221L,
            createdAt = Date(),
            dueAt = null,
            customer = Customer(accountId = AccountFixtures.accounts[1].id, name = AccountFixtures.accounts[1].name),
            taxId = TaxFixtures.taxes[1].id,
            status = InvoiceStatus.PAID,
            currency = "CAD",
            amountPaid = 500.0,
            totalAmount = 500.0,
            amountDue = 0.0,
        ),
        InvoiceSummary(
            id = 104L,
            number = 12039231L,
            createdAt = Date(),
            dueAt = null,
            customer = Customer(accountId = AccountFixtures.accounts[2].id, name = AccountFixtures.accounts[2].name),
            taxId = TaxFixtures.taxes[1].id,
            status = InvoiceStatus.VOIDED,
            currency = "CAD",
            amountPaid = 500.0,
            totalAmount = 500.0,
            amountDue = 0.0,
        ),
    )

    val invoice = Invoice(
        id = 103L,
        pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
        number = 12039221L,
        createdAt = Date(),
        invoicedAt = Date(),
        dueAt = DateUtils.addDays(Date(), 10),
        customer = Customer(name = "Ray Sponsible"),
        taxId = TaxFixtures.taxes[1].id,
        status = InvoiceStatus.OPENED,
        currency = "CAD",
        amountPaid = 800.0,
        totalAmount = 840.0,
        amountDue = 40.0,
        subTotalAmount = 800.0,
        totalTaxAmount = 40.0,
        description = "This is an example of description",
        modifiedAt = Date(),
        modifiedById = UserFixtures.users[0].id,
        createdById = UserFixtures.users[0].id,
        billingAddress = Address(
            street = "340 Pascal",
            postalCode = "H7K 1C7",
            cityId = RefDataFixtures.locations[2].id,
            stateId = RefDataFixtures.locations[2].parentId,
            country = "CA",
        ),
        shippingAddress = Address(
            street = "333 Nicolet",
            postalCode = "111 111",
            cityId = RefDataFixtures.locations[3].id,
            stateId = RefDataFixtures.locations[3].parentId,
            country = "CA",
        ),
        items = listOf(
            InvoiceItem(
                id = 10300,
                productId = ProductFixtures.products[0].id,
                unitId = RefDataFixtures.units[0].id,
                unitPriceId = ProductFixtures.prices[0].id,
                description = ProductFixtures.products[0].name,
                unitPrice = 300.0,
                quantity = 2,
                subTotal = 600.0,
                currency = "CAD",
                taxes = listOf(
                    InvoiceSalesTax(
                        id = 1030000,
                        salesTaxId = RefDataFixtures.salesTaxes[1].id,
                        rate = 5.0,
                        amount = 10.0,
                        currency = "CAD",
                    ),
                    InvoiceSalesTax(
                        id = 1030000,
                        salesTaxId = RefDataFixtures.salesTaxes[2].id,
                        rate = 9.975,
                        amount = 25.0,
                        currency = "CAD",
                    ),
                )
            ),
            InvoiceItem(
                id = 10301,
                productId = ProductFixtures.products[1].id,
                unitId = RefDataFixtures.units[1].id,
                unitPriceId = ProductFixtures.prices[1].id,
                description = ProductFixtures.products[1].name,
                unitPrice = 50.0,
                quantity = 4,
                subTotal = 200.0,
                currency = "CAD",
                taxes = listOf(
                    InvoiceSalesTax(
                        id = 1030000,
                        salesTaxId = RefDataFixtures.salesTaxes[1].id,
                        rate = 5.0,
                        amount = 5.0,
                        currency = "CAD",
                    ),
                )
            ),
        ),
        taxes = listOf(
            InvoiceSalesTax(
                id = 1030000,
                salesTaxId = RefDataFixtures.salesTaxes[1].id,
                rate = 5.0,
                amount = 15.0,
                currency = "CAD",
            ),
            InvoiceSalesTax(
                id = 1030000,
                salesTaxId = RefDataFixtures.salesTaxes[2].id,
                rate = 9.975,
                amount = 25.0,
                currency = "CAD",
            ),
        )
    )
}
