package com.wutsi.koki.invoice.server.mapper

import com.wutsi.koki.invoice.dto.Customer
import com.wutsi.koki.invoice.dto.Invoice
import com.wutsi.koki.invoice.dto.InvoiceItem
import com.wutsi.koki.invoice.dto.InvoiceSalesTax
import com.wutsi.koki.invoice.dto.InvoiceSummary
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.refdata.dto.Address
import org.springframework.stereotype.Service

@Service
class InvoiceMapper {
    fun toInvoice(entity: InvoiceEntity): Invoice {
        return Invoice(
            id = entity.id!!,
            number = entity.number,
            orderId = entity.orderId,
            paynowId = entity.paynowId,
            status = entity.status,
            currency = entity.currency,
            description = entity.description,
            totalAmount = entity.totalAmount,
            totalTaxAmount = entity.totalTaxAmount,
            totalDiscountAmount = entity.totalDiscountAmount,
            subTotalAmount = entity.subTotalAmount,
            amountDue = entity.amountDue,
            amountPaid = entity.amountPaid,
            modifiedAt = entity.modifiedAt,
            createdAt = entity.createdAt,
            invoicedAt = entity.invoicedAt,
            dueAt = entity.dueAt,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
            locale = entity.locale,
            shippingAddress = if (entity.hasShippingAddress()) {
                Address(
                    street = entity.shippingStreet,
                    postalCode = entity.shippingPostalCode,
                    cityId = entity.shippingCityId,
                    stateId = entity.shippingStateId,
                    country = entity.shippingCountry,
                )
            } else {
                null
            },
            billingAddress = if (entity.hasBillingAddress()) {
                Address(
                    street = entity.billingStreet,
                    postalCode = entity.billingPostalCode,
                    cityId = entity.billingCityId,
                    stateId = entity.billingStateId,
                    country = entity.billingCountry,
                )
            } else {
                null
            },
            customer = Customer(
                accountId = entity.customerAccountId,
                name = entity.customerName,
                phone = entity.customerPhone,
                mobile = entity.customerMobile,
                email = entity.customerEmail
            ),
            items = entity.items.map { item ->
                InvoiceItem(
                    id = item.id!!,
                    unitPriceId = item.unitPriceId,
                    unitId = item.unitId,
                    productId = item.productId,
                    description = item.description,
                    currency = item.currency,
                    quantity = item.quantity,
                    unitPrice = item.unitPrice,
                    subTotal = item.subTotal,
                    taxes = item.taxes.map { tax ->
                        InvoiceSalesTax(
                            id = tax.id!!,
                            salesTaxId = tax.salesTaxId,
                            rate = tax.rate,
                            amount = tax.amount,
                            currency = tax.currency,
                        )
                    }
                )
            },
            taxes = entity.items.flatMap { item -> item.taxes }
                .groupBy { tax -> tax.salesTaxId }
                .map { entry ->
                    InvoiceSalesTax(
                        id = -1,
                        salesTaxId = entry.key,
                        rate = entry.value[0].rate,
                        currency = entry.value[0].currency,
                        amount = entry.value.sumOf { it.amount }
                    )
                }
        )
    }

    fun toInvoiceSummary(entity: InvoiceEntity): InvoiceSummary {
        return InvoiceSummary(
            id = entity.id!!,
            number = entity.number,
            orderId = entity.orderId,
            paynowId = entity.paynowId,
            status = entity.status,
            currency = entity.currency,
            totalAmount = entity.totalAmount,
            amountDue = entity.amountDue,
            amountPaid = entity.amountPaid,
            modifiedAt = entity.modifiedAt,
            createdAt = entity.createdAt,
            invoicedAt = entity.invoicedAt,
            dueAt = entity.dueAt,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
            customer = Customer(
                accountId = entity.customerAccountId,
                name = entity.customerName,
                phone = entity.customerPhone,
                mobile = entity.customerMobile,
                email = entity.customerEmail
            ),
        )
    }
}
