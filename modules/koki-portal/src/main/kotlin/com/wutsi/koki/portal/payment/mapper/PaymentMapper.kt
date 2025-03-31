package com.wutsi.koki.portal.payment.mapper

import com.wutsi.koki.payment.dto.PaymentMethod
import com.wutsi.koki.payment.dto.PaymentMethodCash
import com.wutsi.koki.payment.dto.PaymentMethodCheck
import com.wutsi.koki.payment.dto.PaymentMethodInterac
import com.wutsi.koki.payment.dto.Transaction
import com.wutsi.koki.payment.dto.TransactionSummary
import com.wutsi.koki.portal.common.mapper.MoneyMapper
import com.wutsi.koki.portal.invoice.model.InvoiceModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.payment.model.PaymentMethodCashModel
import com.wutsi.koki.portal.payment.model.PaymentMethodCheckModel
import com.wutsi.koki.portal.payment.model.PaymentMethodInteracModel
import com.wutsi.koki.portal.payment.model.PaymentMethodModel
import com.wutsi.koki.portal.payment.model.TransactionModel
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class PaymentMapper(private val moneyMapper: MoneyMapper) : TenantAwareMapper() {
    fun toTransactionModel(
        entity: Transaction,
        users: Map<Long, UserModel>,
        invoices: Map<Long, InvoiceModel>
    ): TransactionModel {
        val fmt = createDateTimeFormat()
        return TransactionModel(
            id = entity.id,
            invoice = invoices[entity.invoiceId] ?: InvoiceModel(id = entity.invoiceId),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            status = entity.status,
            type = entity.type,
            paymentMethodType = entity.paymentMethodType,
            amount = moneyMapper.toMoneyModel(entity.amount, entity.currency),
            errorCode = entity.errorCode,
            createdBy = users[entity.createdById],
            description = entity.description,
            gateway = entity.gateway,
            supplierErrorCode = entity.supplierErrorCode,
            supplierTransactionId = entity.supplierTransactionId,
            supplierErrorMessage = entity.supplierErrorMessage,
            supplierStatus = entity.supplierStatus,
            paymentMethod = toPaymentMethodModel(
                entity = entity.paymentMethod,
                users = users
            ),
        )
    }

    fun toTransactionModel(
        entity: TransactionSummary,
        users: Map<Long, UserModel>,
        invoices: Map<Long, InvoiceModel>
    ): TransactionModel {
        val fmt = createDateTimeFormat()
        return TransactionModel(
            id = entity.id,
            invoice = invoices[entity.invoiceId] ?: InvoiceModel(id = entity.invoiceId),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            status = entity.status,
            type = entity.type,
            paymentMethodType = entity.paymentMethodType,
            amount = moneyMapper.toMoneyModel(entity.amount, entity.currency),
            errorCode = entity.errorCode,
            createdBy = users[entity.createdById],
        )
    }

    fun toPaymentMethodModel(
        entity: PaymentMethod,
        users: Map<Long, UserModel>
    ): PaymentMethodModel {
        return PaymentMethodModel(
            cash = entity.cash?.let { cash -> toPaymentMethodCashModel(cash, users) },
            interac = entity.interac?.let { interac -> toPaymentMethodInteracModel(interac) },
            check = entity.check?.let { check -> toPaymentMethodCheckModel(check) }
        )
    }

    fun toPaymentMethodCashModel(
        entity: PaymentMethodCash,
        users: Map<Long, UserModel>
    ): PaymentMethodCashModel {
        val fmt = createDateFormat()
        return PaymentMethodCashModel(
            id = entity.id,
            collectedBy = entity.collectedById?.let { id -> users[id] },
            collectedAt = entity.collectedAt,
            collectedAtText = entity.collectedAt?.let { date -> fmt.format(date) },
        )
    }

    fun toPaymentMethodInteracModel(entity: PaymentMethodInterac): PaymentMethodInteracModel {
        val fmt = createDateFormat()
        return PaymentMethodInteracModel(
            id = entity.id,
            referenceNumber = entity.referenceNumber,
            sentAt = entity.sentAt,
            sentAtText = entity.sentAt?.let { date -> fmt.format(date) },
            clearedAt = entity.clearedAt,
            clearedAtText = entity.clearedAt?.let { date -> fmt.format(date) },
            bankName = entity.bankName,
        )
    }

    fun toPaymentMethodCheckModel(entity: PaymentMethodCheck): PaymentMethodCheckModel {
        val fmt = createDateFormat()
        return PaymentMethodCheckModel(
            id = entity.id,
            bankName = entity.bankName,
            checkNumber = entity.checkNumber,
            clearedAt = entity.clearedAt,
            clearedAtText = entity.clearedAt?.let { date -> fmt.format(date) },
            checkDate = entity.checkDate,
            checkDateText = entity.checkDate?.let { date -> fmt.format(date) },
        )
    }
}
