package com.wutsi.koki.invoice.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.server.dao.InvoiceItemRepository
import com.wutsi.koki.invoice.server.dao.InvoiceRepository
import com.wutsi.koki.invoice.server.dao.InvoiceSequenceRepository
import com.wutsi.koki.invoice.server.dao.InvoiceTaxRepository
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.domain.InvoiceItemEntity
import com.wutsi.koki.invoice.server.domain.InvoiceSequenceEntity
import com.wutsi.koki.invoice.server.domain.InvoiceTaxEntity
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.SalesTaxEntity
import com.wutsi.koki.refdata.server.service.JuridictionService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.refdata.server.service.SalesTaxService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class InvoiceService(
    private val dao: InvoiceRepository,
    private val itemDao: InvoiceItemRepository,
    private val taxDao: InvoiceTaxRepository,
    private val seqDao: InvoiceSequenceRepository,
    private val securityService: SecurityService,
    private val locationService: LocationService,
    private val businessService: BusinessService,
    private val juridictionService: JuridictionService,
    private val salesTaxService: SalesTaxService,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): InvoiceEntity {
        val tax = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.INVOICE_NOT_FOUND)) }

        if (tax.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.INVOICE_NOT_FOUND))
        }
        return tax
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        number: Long? = null,
        statuses: List<InvoiceStatus> = emptyList(),
        accountId: Long? = null,
        taxId: Long? = null,
        orderId: Long? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<InvoiceEntity> {
        val jql = StringBuilder("SELECT I FROM InvoiceEntity I WHERE I.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND I.id IN :ids")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND I.status IN :statuses")
        }
        if (number != null) {
            jql.append(" AND I.number = :number")
        }
        if (accountId != null) {
            jql.append(" AND I.customerAccountId = :accountId")
        }
        if (taxId != null) {
            jql.append(" AND I.taxId = :taxId")
        }
        if (orderId != null) {
            jql.append(" AND I.orderId = :orderId")
        }
        jql.append(" ORDER BY I.id DESC")

        val query = em.createQuery(jql.toString(), InvoiceEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }
        if (number != null) {
            query.setParameter("number", number)
        }
        if (accountId != null) {
            query.setParameter("accountId", accountId)
        }
        if (taxId != null) {
            query.setParameter("taxId", taxId)
        }
        if (orderId != null) {
            query.setParameter("orderId", orderId)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateInvoiceRequest, tenantId: Long): InvoiceEntity {
        val invoice = createInvoice(request, tenantId)
        addItems(request, invoice)
        applyTaxes(invoice)
        return computeTotals(invoice)
    }

    private fun createInvoice(request: CreateInvoiceRequest, tenantId: Long): InvoiceEntity {
        val number = generateNumber(tenantId)
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        val shippingCity = request.shippingCityId?.let { id -> locationService.get(id, LocationType.CITY) }
        val billingCity = request.billingCityId?.let { id -> locationService.get(id, LocationType.CITY) }

        // Invoices
        return dao.save(
            InvoiceEntity(
                tenantId = tenantId,
                taxId = request.taxId,
                orderId = request.orderId,
                description = request.description,

                customerAccountId = request.customerAccountId,
                customerName = request.customerName,
                customerEmail = request.customerEmail,
                customerPhone = request.customerPhone,
                customerMobile = request.customerMobile,

                status = InvoiceStatus.DRAFT,
                currency = request.currency,
                number = number,

                shippingStreet = request.shippingStreet,
                shippingCityId = shippingCity?.id,
                shippingStateId = shippingCity?.parentId,
                shippingCountry = (shippingCity?.country ?: request.shippingCountry)?.uppercase(),
                shippingPostalCode = request.shippingPostalCode,

                billingStreet = request.billingStreet,
                billingCityId = billingCity?.id,
                billingStateId = billingCity?.parentId,
                billingCountry = (billingCity?.country ?: request.billingCountry)?.uppercase(),
                billingPostalCode = request.billingPostalCode,

                createdAt = now,
                createdById = userId,
                modifiedAt = now,
                modifiedById = userId,
                dueAt = request.dueAt,
            )
        )
    }

    private fun addItems(
        request: CreateInvoiceRequest,
        invoice: InvoiceEntity,
    ) {
        invoice.items = request.items.map { item ->
            itemDao.save(
                InvoiceItemEntity(
                    invoice = invoice,
                    productId = item.productId,
                    unitPriceId = item.unitPriceId,
                    unitId = item.unitId,
                    description = item.description,
                    unitPrice = item.unitPrice,
                    quantity = item.quantity,
                    subTotal = item.quantity * item.unitPrice,
                    currency = request.currency,
                )
            )
        }
    }

    private fun applyTaxes(invoice: InvoiceEntity) {
        val business = try {
            businessService.get(tenantId = invoice.tenantId)
        } catch (ex: NotFoundException) {
            null
        }

        if (business != null) {
            applyTaxes(invoice, business)
        }
    }

    private fun applyTaxes(
        invoice: InvoiceEntity,
        business: BusinessEntity,
    ) {
        val juridiction = juridictionService.findJuridiction(
            stateId = invoice.shippingStateId,
            country = invoice.shippingCountry,
            juridictions = business.juridictions
        )
            ?: return

        val salesTaxes = salesTaxService.search(
            juridictionIds = listOf(juridiction.id!!),
            limit = Integer.MAX_VALUE
        )
        invoice.items.forEach { item -> applyTaxes(item, salesTaxes) }
    }

    private fun applyTaxes(
        item: InvoiceItemEntity,
        salesTaxes: List<SalesTaxEntity>,
    ) {
        item.taxes = salesTaxes
            .sortedBy { salesTax -> salesTax.priority }
            .map { salesTax ->
                taxDao.save(
                    InvoiceTaxEntity(
                        invoiceItem = item,
                        salesTaxId = salesTax.id!!,
                        rate = salesTax.rate,
                        amount = (item.subTotal * salesTax.rate) / 100,
                        currency = item.currency,
                    )
                )
            }
    }

    private fun computeTotals(invoice: InvoiceEntity): InvoiceEntity {
        invoice.subTotalAmount = invoice.items.sumOf { item -> item.subTotal }
        invoice.totalTaxAmount = invoice.items.sumOf { item -> item.taxes.sumOf { tax -> tax.amount } }
        invoice.totalAmount = invoice.subTotalAmount + invoice.totalTaxAmount - invoice.totalDiscountAmount
        return dao.save(invoice)
    }

    private fun generateNumber(tenantId: Long): Long {
        val seq = seqDao.findByTenantId(tenantId)
        if (seq == null) {
            seqDao.save(InvoiceSequenceEntity(tenantId = tenantId, current = 1))
            return 1L
        } else {
            seq.current = seq.current + 1
            seqDao.save(seq)
            return seq.current
        }
    }
}
