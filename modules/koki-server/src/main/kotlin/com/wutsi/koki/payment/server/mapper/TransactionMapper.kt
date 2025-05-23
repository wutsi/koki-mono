package com.wutsi.koki.payment.server.mapper

import com.wutsi.koki.payment.dto.PaymentMethod
import com.wutsi.koki.payment.dto.PaymentMethodCash
import com.wutsi.koki.payment.dto.PaymentMethodCheck
import com.wutsi.koki.payment.dto.PaymentMethodInterac
import com.wutsi.koki.payment.dto.Transaction
import com.wutsi.koki.payment.dto.TransactionSummary
import com.wutsi.koki.payment.server.domain.PaymentMethodCashEntity
import com.wutsi.koki.payment.server.domain.PaymentMethodCheckEntity
import com.wutsi.koki.payment.server.domain.PaymentMethodInteracEntity
import com.wutsi.koki.payment.server.domain.TransactionEntity
import org.springframework.stereotype.Service

@Service
class TransactionMapper {
    fun toTransactionSummary(entity: TransactionEntity): TransactionSummary {
        return TransactionSummary(
            id = entity.id!!,
            invoiceId = entity.invoiceId,
            createdById = entity.createdById,
            type = entity.type,
            paymentMethodType = entity.paymentMethodType,
            status = entity.status,
            amount = entity.amount,
            currency = entity.currency,
            createdAt = entity.createdAt,
            errorCode = entity.errorCode,
        )
    }

    fun toTransaction(
        entity: TransactionEntity,
        cash: PaymentMethodCashEntity?,
        interact: PaymentMethodInteracEntity?,
        check: PaymentMethodCheckEntity?
    ): Transaction {
        return Transaction(
            id = entity.id!!,
            invoiceId = entity.invoiceId,
            createdById = entity.createdById,
            type = entity.type,
            paymentMethodType = entity.paymentMethodType,
            status = entity.status,
            amount = entity.amount,
            currency = entity.currency,
            createdAt = entity.createdAt,
            errorCode = entity.errorCode,
            description = entity.description,
            gateway = entity.gateway,
            supplierTransactionId = entity.supplierTransactionId,
            supplierStatus = entity.supplierStatus,
            supplierErrorCode = entity.supplierErrorCode,
            supplierErrorMessage = entity.supplierErrorMessage,
            paymentMethod = PaymentMethod(
                cash = cash?.let { toPaymentMethodCash(cash) },
                interac = interact?.let { toPaymentMethodInteract(interact) },
                check = check?.let { toPaymentMethodCheck(check) },
            )
        )
    }

    private fun toPaymentMethodCash(entity: PaymentMethodCashEntity): PaymentMethodCash {
        return PaymentMethodCash(
            id = entity.id!!,
            collectedAt = entity.collectedAt,
            collectedById = entity.collectedById,
        )
    }

    private fun toPaymentMethodInteract(entity: PaymentMethodInteracEntity): PaymentMethodInterac {
        return PaymentMethodInterac(
            id = entity.id!!,
            referenceNumber = entity.referenceNumber,
            sentAt = entity.sentAt,
            clearedAt = entity.clearedAt,
            bankName = entity.bankName,
        )
    }

    private fun toPaymentMethodCheck(entity: PaymentMethodCheckEntity): PaymentMethodCheck {
        return PaymentMethodCheck(
            id = entity.id!!,
            bankName = entity.bankName,
            checkNumber = entity.checkNumber,
            clearedAt = entity.clearedAt,
            checkDate = entity.checkDate,
        )
    }
}
