package com.wutsi.koki.payment.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.payment.dto.CreateCashPaymentRequest
import com.wutsi.koki.payment.dto.CreateCheckPaymentRequest
import com.wutsi.koki.payment.dto.CreateInteracPaymentRequest
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.payment.server.dao.PaymentMethodCashRepository
import com.wutsi.koki.payment.server.dao.PaymentMethodCheckRepository
import com.wutsi.koki.payment.server.dao.PaymentMethodInteractRepository
import com.wutsi.koki.payment.server.dao.TransactionRepository
import com.wutsi.koki.payment.server.domain.PaymentMethodCashEntity
import com.wutsi.koki.payment.server.domain.PaymentMethodCheckEntity
import com.wutsi.koki.payment.server.domain.PaymentMethodInteractEntity
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class PaymentService(
    private val dao: TransactionRepository,
    private val cashDao: PaymentMethodCashRepository,
    private val interactDao: PaymentMethodInteractRepository,
    private val checkDao: PaymentMethodCheckRepository,
    private val securityService: SecurityService,
) {
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

    fun getInteracByTransactionId(transactionId: String): PaymentMethodInteractEntity {
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
    fun interact(request: CreateInteracPaymentRequest, tenantId: Long): TransactionEntity {
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
            PaymentMethodInteractEntity(
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
                clearedAt = request.clearedAt,
                checkNumber = request.checkNumber,
                bankName = request.bankName,
            )
        )
        return tx
    }
}
