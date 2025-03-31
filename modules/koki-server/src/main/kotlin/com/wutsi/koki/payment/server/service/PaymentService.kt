package com.wutsi.koki.payment.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.error.exception.WutsiException
import com.wutsi.koki.invoice.server.service.InvoiceService
import com.wutsi.koki.payment.dto.CreateCashPaymentRequest
import com.wutsi.koki.payment.dto.CreateCheckPaymentRequest
import com.wutsi.koki.payment.dto.CreateInteracPaymentRequest
import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.PrepareCheckoutRequest
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.payment.server.dao.PaymentMethodCashRepository
import com.wutsi.koki.payment.server.dao.PaymentMethodCheckRepository
import com.wutsi.koki.payment.server.dao.PaymentMethodInteracRepository
import com.wutsi.koki.payment.server.dao.TransactionRepository
import com.wutsi.koki.payment.server.domain.PaymentMethodCashEntity
import com.wutsi.koki.payment.server.domain.PaymentMethodCheckEntity
import com.wutsi.koki.payment.server.domain.PaymentMethodInteracEntity
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class PaymentService(
    private val dao: TransactionRepository,
    private val cashDao: PaymentMethodCashRepository,
    private val interactDao: PaymentMethodInteracRepository,
    private val checkDao: PaymentMethodCheckRepository,
    private val securityService: SecurityService,
    private val invoiceService: InvoiceService,
    private val configurationService: ConfigurationService,
    private val paymentGatewayServiceProvider: PaymentGatewayServiceProvider,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaymentService::class.java)
    }

    fun getCashByTransactionId(transactionId: String): PaymentMethodCashEntity {
        val paymentMethod = cashDao.findByTransactionId(transactionId)
        if (paymentMethod == null) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_FOUND,
                )
            )
        }
        return paymentMethod
    }

    fun getCheckByTransactionId(transactionId: String): PaymentMethodCheckEntity {
        val paymentMethod = checkDao.findByTransactionId(transactionId)
        if (paymentMethod == null) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_FOUND,
                )
            )
        }
        return paymentMethod
    }

    fun getInteracByTransactionId(transactionId: String): PaymentMethodInteracEntity {
        val paymentMethod = interactDao.findByTransactionId(transactionId)
        if (paymentMethod == null) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_FOUND,
                )
            )
        }
        return paymentMethod
    }

    @Transactional
    fun cash(request: CreateCashPaymentRequest, tenantId: Long): TransactionEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        val tx = dao.save(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                invoiceId = request.invoiceId,
                tenantId = tenantId,
                type = TransactionType.PAYMENT,
                paymentMethodType = PaymentMethodType.CASH,
                status = TransactionStatus.SUCCESSFUL,
                currency = request.currency,
                amount = request.amount,
                description = request.description,
                createdById = userId,
                createdAt = now,
                modifiedAt = now,
            )
        )
        cashDao.save(
            PaymentMethodCashEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                transactionId = tx.id!!,
                collectedAt = request.collectedAt,
                collectedById = request.collectedById,
            )
        )
        return tx
    }

    @Transactional
    fun interac(request: CreateInteracPaymentRequest, tenantId: Long): TransactionEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        val tx = dao.save(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                invoiceId = request.invoiceId,
                tenantId = tenantId,
                type = TransactionType.PAYMENT,
                paymentMethodType = PaymentMethodType.INTERAC,
                status = TransactionStatus.SUCCESSFUL,
                currency = request.currency,
                amount = request.amount,
                description = request.description,
                createdById = userId,
                createdAt = now,
                modifiedAt = now,
            )
        )
        interactDao.save(
            PaymentMethodInteracEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                transactionId = tx.id!!,
                sentAt = request.sentAt,
                clearedAt = request.clearedAt,
                referenceNumber = request.referenceNumber,
                bankName = request.bankName,
            )
        )
        return tx
    }

    @Transactional
    fun check(request: CreateCheckPaymentRequest, tenantId: Long): TransactionEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        val tx = dao.save(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                invoiceId = request.invoiceId,
                tenantId = tenantId,
                type = TransactionType.PAYMENT,
                paymentMethodType = PaymentMethodType.CHECK,
                status = TransactionStatus.SUCCESSFUL,
                currency = request.currency,
                amount = request.amount,
                description = request.description,
                createdById = userId,
                createdAt = now,
                modifiedAt = now,
            )
        )
        checkDao.save(
            PaymentMethodCheckEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                transactionId = tx.id!!,
                checkDate = request.checkDate,
                clearedAt = request.clearedAt,
                checkNumber = request.checkNumber,
                bankName = request.bankName,
            )
        )
        return tx
    }

    @Transactional
    fun checkout(request: PrepareCheckoutRequest, tenantId: Long): TransactionEntity {
        // invoice
        val invoice = invoiceService.get(request.invoiceId, tenantId)
        if (request.paynowId != null && invoice.paynowId != request.paynowId) {
            throw NotFoundException(
                error = Error(code = ErrorCode.INVOICE_NOT_FOUND)
            )
        }

        // Create transaction
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        val tx = dao.save(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                invoiceId = request.invoiceId,
                tenantId = tenantId,
                type = TransactionType.PAYMENT,
                paymentMethodType = request.paymentMethodType,
                gateway = PaymentGateway.UNKNOWN,
                status = TransactionStatus.PENDING,
                currency = invoice.currency,
                amount = invoice.totalAmount,
                description = invoice.description,
                createdById = userId,
                createdAt = now,
                modifiedAt = now,
            )
        )

        // Checkout
        try {
            tx.gateway = getPaymentGateway(request.paymentMethodType, tenantId)
            val gatewayService = paymentGatewayServiceProvider.get(tx.gateway)
            gatewayService.checkout(tx)
        } catch (ex: PaymentGatewayException) {
            LOGGER.warn("Payment failed", ex)

            tx.status = TransactionStatus.FAILED
            tx.errorCode = ex.errorCode
            tx.supplierErrorCode = ex.supplierErrorCode
            tx.supplierErrorMessage = ex.message
        } catch (ex: WutsiException) {
            LOGGER.warn("Payment failed", ex)

            tx.status = TransactionStatus.FAILED
            tx.errorCode = ex.error.code
            tx.supplierErrorMessage = ex.message
        } catch (ex: Throwable) {
            LOGGER.warn("Payment failed", ex)

            tx.status = TransactionStatus.FAILED
            tx.errorCode = ErrorCode.TRANSACTION_PAYMENT_FAILED
            tx.supplierErrorMessage = ex.message
        }

        dao.save(tx)
        return tx
    }

    private fun getPaymentGateway(type: PaymentMethodType, tenantId: Long): PaymentGateway {
        val config = configurationService.search(keyword = "payment.", tenantId = tenantId)
            .map { cfg -> cfg.name to cfg.value }
            .toMap()

        val gateways = PaymentGateway.entries
            .filter { gateway -> gateway.paymentMethodType == type }
            .filter { gateway ->
                config["payment.method.${type.name.lowercase()}.gateway"] == gateway.name &&
                    config["payment.method.${type.name.lowercase()}.enabled"] != null
            }

        return gateways.firstOrNull()
            ?: throw ConflictException(
                error = Error(
                    code = ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_SUPPORTED,
                    parameter = Parameter(value = type.name),
                )
            )
    }
}
