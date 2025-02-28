package com.wutsi.koki.portal.invoice.mapper

import com.wutsi.koki.invoice.dto.Customer
import com.wutsi.koki.invoice.dto.Invoice
import com.wutsi.koki.invoice.dto.InvoiceItem
import com.wutsi.koki.invoice.dto.InvoiceSalesTax
import com.wutsi.koki.invoice.dto.InvoiceSummary
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.common.mapper.MoneyMapper
import com.wutsi.koki.portal.invoice.model.CustomerModel
import com.wutsi.koki.portal.invoice.model.InvoiceItemModel
import com.wutsi.koki.portal.invoice.model.InvoiceModel
import com.wutsi.koki.portal.invoice.model.InvoiceSalesTaxModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.product.model.ProductModel
import com.wutsi.koki.portal.refdata.mapper.RefDataMapper
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.refdata.model.SalesTaxModel
import com.wutsi.koki.portal.refdata.model.UnitModel
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class InvoiceMapper(
    private val moneyMapper: MoneyMapper,
    private val refDataMapper: RefDataMapper,
) : TenantAwareMapper() {
    fun toInvoiceModel(
        entity: InvoiceSummary,
        users: Map<Long, UserModel>,
        accounts: Map<Long, AccountModel>,
    ): InvoiceModel {
        val fmt = createDateTimeFormat()
        val dateFormat = createDateFormat()
        return InvoiceModel(
            id = entity.id,
            taxId = entity.taxId,
            orderId = entity.orderId,
            number = entity.number,
            status = entity.status,

            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = dateFormat.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
            dueAt = entity.createdAt,
            dueAtText = entity.dueAt?.let { date -> dateFormat.format(date) },

            totalAmount = moneyMapper.toMoneyModel(entity.totalAmount, entity.currency),
            amountPaid = moneyMapper.toMoneyModel(entity.amountPaid, entity.currency),
            amountDue = moneyMapper.toMoneyModel(entity.amountDue, entity.currency),
            customer = toCustomerModel(entity.customer, accounts),
        )
    }

    fun toInvoiceModel(
        entity: Invoice,
        accounts: Map<Long, AccountModel>,
        users: Map<Long, UserModel>,
        locations: Map<Long, LocationModel>,
        units: Map<Long, UnitModel>,
        products: Map<Long, ProductModel>,
        salesTaxes: Map<Long, SalesTaxModel>,
    ): InvoiceModel {
        val fmt = createDateTimeFormat()
        val dateFormat = createDateFormat()

        val invoiceTaxes = entity.items.flatMap { item -> item.taxes }
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

        return InvoiceModel(
            id = entity.id,
            taxId = entity.taxId,
            orderId = entity.orderId,
            number = entity.number,
            status = entity.status,
            description = entity.description,

            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = dateFormat.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
            dueAt = entity.createdAt,
            dueAtText = entity.dueAt?.let { date -> dateFormat.format(date) },

            subTotalAmount = moneyMapper.toMoneyModel(entity.subTotalAmount, entity.currency),
            totalTaxAmount = moneyMapper.toMoneyModel(entity.totalTaxAmount, entity.currency),
            totalDiscountAmount = moneyMapper.toMoneyModel(entity.totalDiscountAmount, entity.currency),
            totalAmount = moneyMapper.toMoneyModel(entity.totalAmount, entity.currency),
            amountPaid = moneyMapper.toMoneyModel(entity.amountPaid, entity.currency),
            amountDue = moneyMapper.toMoneyModel(entity.amountDue, entity.currency),

            customer = toCustomerModel(entity.customer, accounts),
            shippingAddress = entity.shippingAddress?.let { addr -> refDataMapper.toAddressModel(addr, locations) },
            billingAddress = entity.billingAddress?.let { addr -> refDataMapper.toAddressModel(addr, locations) },
            items = entity.items.map { item -> toInvoiceItemModel(item, units, products, salesTaxes) },
            taxes = invoiceTaxes.map { tax -> toInvoiceSalesTaxModel(tax, salesTaxes) }
        )
    }

    fun toInvoiceItemModel(
        entity: InvoiceItem,
        units: Map<Long, UnitModel>,
        products: Map<Long, ProductModel>,
        salesTaxes: Map<Long, SalesTaxModel>,
    ): InvoiceItemModel {
        return InvoiceItemModel(
            id = entity.id,
            description = entity.description,
            quantity = entity.quantity,
            unit = entity.unitId?.let { id -> units[id] },
            unitPrice = moneyMapper.toMoneyModel(entity.unitPrice, entity.currency),
            subTotal = moneyMapper.toMoneyModel(entity.subTotal, entity.currency),
            product = products[entity.productId] ?: ProductModel(id = entity.productId),
            taxes = entity.taxes.map { tax -> toInvoiceSalesTaxModel(tax, salesTaxes) }
        )
    }

    fun toInvoiceSalesTaxModel(
        entity: InvoiceSalesTax,
        salesTaxes: Map<Long, SalesTaxModel>,
    ): InvoiceSalesTaxModel {
        return InvoiceSalesTaxModel(
            id = entity.id,
            salesTax = salesTaxes[entity.salesTaxId] ?: SalesTaxModel(id = entity.salesTaxId),
            rate = entity.rate,
            amount = moneyMapper.toMoneyModel(entity.amount, entity.currency),
        )
    }

    fun toCustomerModel(entity: Customer, accounts: Map<Long, AccountModel>): CustomerModel {
        return CustomerModel(
            name = entity.name,
            email = entity.email,
            phone = entity.phone,
            mobile = entity.mobile,
            account = entity.accountId?.let { id -> accounts[id] },
        )
    }
}
