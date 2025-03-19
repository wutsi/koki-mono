package com.wutsi.koki.invoice.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.UpdateInvoiceStatusRequest
import com.wutsi.koki.invoice.server.dao.InvoiceItemRepository
import com.wutsi.koki.invoice.server.dao.InvoiceLogRepository
import com.wutsi.koki.invoice.server.dao.InvoiceRepository
import com.wutsi.koki.invoice.server.dao.InvoiceSequenceRepository
import com.wutsi.koki.invoice.server.dao.InvoiceTaxRepository
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.domain.InvoiceItemEntity
import com.wutsi.koki.invoice.server.domain.InvoiceLogEntity
import com.wutsi.koki.invoice.server.domain.InvoiceSequenceEntity
import com.wutsi.koki.invoice.server.domain.InvoiceTaxEntity
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.server.dao.TransactionRepository
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.SalesTaxEntity
import com.wutsi.koki.refdata.server.service.JuridictionService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.refdata.server.service.SalesTaxService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import java.util.Date

@Service
class InvoiceService(
    private val dao: InvoiceRepository,
    private val itemDao: InvoiceItemRepository,
    private val taxDao: InvoiceTaxRepository,
    private val seqDao: InvoiceSequenceRepository,
    private val logDao: InvoiceLogRepository,
    private val txDao: TransactionRepository,
    private val securityService: SecurityService,
    private val locationService: LocationService,
    private val businessService: BusinessService,
    private val juridictionService: JuridictionService,
    private val salesTaxService: SalesTaxService,
    private val configurationService: ConfigurationService,
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

    fun getInvoiceItems(invoice: InvoiceEntity): List<InvoiceItemEntity> {
        return itemDao.findByInvoice(invoice)
    }

    fun getInvoiceTaxes(invoice: InvoiceEntity): List<InvoiceTaxEntity> {
        return taxDao.findByInvoice(invoice)
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
    fun status(id: Long, request: UpdateInvoiceStatusRequest, tenantId: Long): InvoiceEntity {
        val invoice = get(id, tenantId)

        // Never change the status of closed invoice
        if (invoice.status == InvoiceStatus.VOIDED || invoice.status == InvoiceStatus.PAID) {
            throw BadRequestException(
                Error(
                    code = ErrorCode.INVOICE_BAD_STATUS,
                    data = mapOf(
                        "invoiceStatus" to invoice.status,
                        "requestStatus" to request.status,
                    )
                )
            )
        }

        // Update the invoice
        if (request.status == InvoiceStatus.OPENED && invoice.status == InvoiceStatus.DRAFT) {
            val now = Date()
            invoice.status = request.status
            invoice.invoicedAt = Date()
            invoice.dueAt = DateUtils.addDays(now, getDueDays(tenantId))
        } else if (request.status == InvoiceStatus.VOIDED && (invoice.status == InvoiceStatus.OPENED || invoice.status == InvoiceStatus.DRAFT)) {
            invoice.status = request.status
        } else if (request.status == InvoiceStatus.PAID && invoice.status == InvoiceStatus.OPENED && invoice.amountDue <= 0) {
            invoice.status = request.status
        } else {
            throw BadRequestException(
                Error(
                    code = ErrorCode.INVOICE_BAD_STATUS,
                    data = mapOf(
                        "invoiceStatus" to invoice.status,
                        "requestStatus" to request.status,
                        "amountDue" to invoice.amountDue,
                    )
                )
            )
        }
        invoice.modifiedById = securityService.getCurrentUserIdOrNull()
        invoice.modifiedAt = Date()
        dao.save(invoice)

        // Log
        recordLog(invoice, request.status, request.comment)

        return invoice
    }

    @Transactional
    fun create(request: CreateInvoiceRequest, tenantId: Long): InvoiceEntity {
        val invoice = createInvoice(request, tenantId)
        val business = businessService.get(tenantId)
        addItems(request, invoice)
        applyTaxes(invoice, business)
        recordLog(invoice, invoice.status, null)

        return computeTotals(invoice)
    }

    @Transactional
    fun onPaymentReceived(id: Long, tenantId: Long): InvoiceEntity {
        /* Update amount due */
        val invoice = get(id, tenantId)
        val transactions = txDao.findByInvoiceIdAndStatus(id, TransactionStatus.SUCCESSFUL)
        val amountPaid = transactions.sumOf { tx -> tx.amount }
        invoice.amountPaid = amountPaid
        invoice.amountDue = invoice.totalAmount - invoice.amountPaid
        dao.save(invoice)

        /* Update status */
        if (invoice.amountDue <= 0.0 && invoice.status == InvoiceStatus.OPENED) {
            status(
                id = id,
                request = UpdateInvoiceStatusRequest(status = InvoiceStatus.PAID),
                tenantId = tenantId,
            )
        }
        return invoice
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
                locale = request.locale,
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

    private fun applyTaxes(invoice: InvoiceEntity, business: BusinessEntity) {
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
        invoice.amountDue = invoice.totalAmount
        return dao.save(invoice)
    }

    private fun generateNumber(tenantId: Long): Long {
        // Generate number
        val seq = seqDao.findByTenantId(tenantId)
        val number = if (seq == null) {
            seqDao.save(InvoiceSequenceEntity(tenantId = tenantId, current = 1))
            1L
        } else {
            seq.current = seq.current + 1
            seqDao.save(seq)
            seq.current
        }

        // Start
        val configs = configurationService.search(
            tenantId = tenantId,
            names = listOf(ConfigurationName.INVOICE_START_NUMBER)
        )
        val start = if (configs.isEmpty()) {
            0L
        } else {
            try {
                configs[0].value.toLong()
            } catch (ex: Exception) {
                0L
            }
        }
        return start + number
    }

    private fun getDueDays(tenantId: Long): Int {
        val configs = configurationService.search(
            tenantId = tenantId,
            names = listOf(ConfigurationName.INVOICE_DUE_DAYS)
        )
        return if (configs.isEmpty()) {
            0
        } else {
            try {
                configs[0].value.toInt()
            } catch (ex: Exception) {
                0
            }
        }
    }

    private fun recordLog(invoice: InvoiceEntity, status: InvoiceStatus, comment: String?): InvoiceLogEntity {
        return logDao.save(
            InvoiceLogEntity(
                invoice = invoice,
                status = status,
                comment = comment,
                createdById = securityService.getCurrentUserIdOrNull(),
                createdAt = Date(),
            )
        )
    }
}
