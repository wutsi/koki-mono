package com.wutsi.koki.portal.invoice.service

import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.UpdateInvoiceStatusRequest
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.invoice.mapper.InvoiceMapper
import com.wutsi.koki.portal.invoice.model.InvoiceModel
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.refdata.service.SalesTaxService
import com.wutsi.koki.portal.refdata.service.UnitService
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
) {
    fun invoice(
        id: Long,
        paynowId: String? = null,
        fullGraph: Boolean = true
    ): InvoiceModel {
        val invoice = koki.invoice(id, paynowId).invoice

        // Account
        val account = if (invoice.customer.accountId == null || !fullGraph) {
            null
        } else {
            accountService.account(invoice.customer.accountId!!, false)
        }

        // Users
        val userIds = listOf(invoice.createdById, invoice.modifiedById).filterNotNull().distinct()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds, limit = userIds.size
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
        ).filterNotNull().distinct()
        val locations = if (locationIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            locationService.locations(
                ids = locationIds,
                limit = locationIds.size,
            ).associateBy { location -> location.id }
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
        val salesTaxIds = invoice.items.flatMap { item -> item.taxes }.map { tax -> tax.salesTaxId }.distinct()
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
            orderId = orderId,
            limit = limit,
            offset = offset,
        ).invoices

        val accountIds = invoices.mapNotNull { invoice -> invoice.customer.accountId }.distinct()
        val accounts = if (accountIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            accountService.accounts(
                ids = accountIds, limit = accountIds.size
            ).associateBy { account -> account.id }
        }

        val userIds =
            invoices.flatMap { invoice -> listOf(invoice.createdById, invoice.modifiedById) }.filterNotNull().distinct()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds, limit = userIds.size
            ).associateBy { user -> user.id }
        }

        return invoices.map { invoice ->
            mapper.toInvoiceModel(
                entity = invoice,
                users = users,
                accounts = accounts,
            )
        }
    }

    fun setStatus(id: Long, status: InvoiceStatus) {
        koki.setStatus(
            id, UpdateInvoiceStatusRequest(
                status = status,
            )
        )
    }

    fun pdfUrl(id: Long): String {
        return koki.pdfUrl(id)
    }

    fun send(id: Long) {
        koki.send(id)
    }
}
