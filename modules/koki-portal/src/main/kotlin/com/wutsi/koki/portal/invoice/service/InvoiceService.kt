package com.wutsi.koki.portal.invoice.service

import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.Item
import com.wutsi.koki.invoice.dto.UpdateInvoiceStatusRequest
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.invoice.mapper.InvoiceMapper
import com.wutsi.koki.portal.invoice.model.InvoiceModel
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.refdata.service.SalesTaxService
import com.wutsi.koki.portal.refdata.service.UnitService
import com.wutsi.koki.portal.tax.model.TaxModel
import com.wutsi.koki.portal.tax.model.TaxProductModel
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.portal.tenant.service.CurrentTenantHolder
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiInvoices
import org.springframework.stereotype.Service
import kotlin.collections.flatMap

@Service
class InvoiceService(
    private val koki: KokiInvoices,
    private val mapper: InvoiceMapper,
    private val accountService: AccountService,
    private val userService: UserService,
    private val unitService: UnitService,
    private val locationService: LocationService,
    private val productService: ProductService,
    private val salesTaxService: SalesTaxService,
    private val currentTenant: CurrentTenantHolder,
    private val taxService: TaxService,
) {
    fun createInvoice(tax: TaxModel, taxProducts: List<TaxProductModel>): Long {
        return koki.create(
            CreateInvoiceRequest(
                taxId = tax.id,

                customerAccountId = tax.account.id,
                customerName = tax.account.name,
                customerEmail = tax.account.email ?: "",
                customerPhone = tax.account.phone,
                customerMobile = tax.account.mobile,

                shippingCountry = tax.account.shippingAddress?.country,
                shippingStreet = tax.account.shippingAddress?.street,
                shippingCityId = tax.account.shippingAddress?.city?.id,
                shippingPostalCode = tax.account.shippingAddress?.postalCode,

                billingCountry = tax.account.billingAddress?.country,
                billingStreet = tax.account.billingAddress?.street,
                billingCityId = tax.account.billingAddress?.city?.id,
                billingPostalCode = tax.account.billingAddress?.postalCode,

                currency = taxProducts.firstOrNull()?.unitPrice?.currency
                    ?: currentTenant.get()!!.currency,

                items = taxProducts.map { taxProduct ->
                    Item(
                        productId = taxProduct.product.id,
                        unitId = taxProduct.product.serviceDetails?.unit?.id,
                        unitPriceId = taxProduct.unitPriceId,
                        unitPrice = taxProduct.unitPrice.value,
                        description = taxProduct.description,
                    )
                }
            )
        ).invoiceId
    }

    fun invoice(id: Long, fullGraph: Boolean = true): InvoiceModel {
        val invoice = koki.invoice(id).invoice

        // Account
        val account = if (invoice.customer.accountId == null || !fullGraph) {
            null
        } else {
            accountService.account(invoice.customer.accountId!!, false)
        }

        // Taxes
        val tax = if (!fullGraph) {
            invoice.taxId?.let { id -> TaxModel(id = id) }
        } else {
            invoice.taxId?.let { id -> taxService.tax(id, false) }
        }

        // Users
        val userIds = listOf(invoice.createdById, invoice.modifiedById)
            .filterNotNull()
            .distinct()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds,
                limit = userIds.size
            ).associateBy { user -> user.id }
        }

        // Units
        val unitIds = invoice.items.mapNotNull { item -> item.unitId }.distinct()
        val units = if (unitIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            unitService.units(unitIds).associateBy { user -> user.id }
        }

        // Locations
        val locationIds = listOf(
            invoice.shippingAddress?.stateId,
            invoice.shippingAddress?.cityId,
            invoice.billingAddress?.stateId,
            invoice.billingAddress?.cityId,
        )
            .filterNotNull()
            .distinct()
        val locations = if (locationIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            locationService.locations(
                ids = locationIds,
                limit = locationIds.size,
            )
                .associateBy { location -> location.id }
        }

        // Products
        val productIds = invoice.items.map { item -> item.productId }.distinct()
        val products = if (productIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            productService.products(
                ids = productIds,
                limit = productIds.size,
                fullGraph = false,
            ).associateBy { product -> product.id }
        }

        // Sales Taxes
        val salesTaxIds = invoice.items.flatMap { item -> item.taxes }
            .map { tax -> tax.salesTaxId }
            .distinct()
        val salesTaxes = if (salesTaxIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            salesTaxService.salesTaxes(
                ids = salesTaxIds,
                limit = salesTaxIds.size,
            ).associateBy { tax -> tax.id }
        }

        return mapper.toInvoiceModel(
            entity = invoice,
            taxes = tax?.let { mapOf(tax.id to tax) } ?: emptyMap(),
            accounts = account?.let { mapOf(account.id to account) } ?: emptyMap(),
            locations = locations,
            units = units,
            users = users,
            products = products,
            salesTaxes = salesTaxes,
        )
    }

    fun invoices(
        ids: List<Long> = emptyList(),
        number: Long? = null,
        statuses: List<InvoiceStatus> = emptyList(),
        accountId: Long? = null,
        taxId: Long? = null,
        orderId: Long? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<InvoiceModel> {
        val invoices = koki.invoices(
            ids = ids,
            number = number,
            statuses = statuses,
            accountId = accountId,
            taxId = taxId,
            orderId = orderId,
            limit = limit,
            offset = offset,
        ).invoices

        val taxIds = invoices.mapNotNull { invoice -> invoice.taxId }.distinct()
        val taxes = if (taxIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            taxService.taxes(
                ids = taxIds,
                limit = taxIds.size,
                fullGraph = false,
            ).associateBy { invoice -> invoice.id }
        }

        val accountIds = invoices.mapNotNull { invoice -> invoice.customer.accountId }.distinct()
        val accounts = if (accountIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            accountService.accounts(
                ids = accountIds,
                limit = accountIds.size
            ).associateBy { account -> account.id }
        }

        val userIds = invoices.flatMap { invoice -> listOf(invoice.createdById, invoice.modifiedById) }
            .filterNotNull()
            .distinct()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds,
                limit = userIds.size
            ).associateBy { user -> user.id }
        }

        return invoices.map { invoice ->
            mapper.toInvoiceModel(
                entity = invoice,
                taxes = taxes,
                users = users,
                accounts = accounts,
            )
        }
    }

    fun setStatus(id: Long, status: InvoiceStatus) {
        koki.setStatus(
            id,
            UpdateInvoiceStatusRequest(
                status = status,
            )
        )
    }

    fun url(id: Long): String {
        return koki.url(id)
    }

    fun send(id: Long) {
        koki.send(id)
    }
}
