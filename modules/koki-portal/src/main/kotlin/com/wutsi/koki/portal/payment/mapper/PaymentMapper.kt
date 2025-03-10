package com.wutsi.koki.portal.payment.mapper

import com.wutsi.koki.payment.dto.PaymentMethod
import com.wutsi.koki.payment.dto.PaymentMethodCash
import com.wutsi.koki.payment.dto.Transaction
import com.wutsi.koki.payment.dto.TransactionSummary
import com.wutsi.koki.portal.common.mapper.MoneyMapper
import com.wutsi.koki.portal.invoice.model.InvoiceModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.payment.model.PaymentMethodCashModel
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
            cash = entity.cash?.let { cash -> toPaymentMethodCashModel(cash, users) }
        )
    }

    fun toPaymentMethodCashModel(
        entity: PaymentMethodCash,
        users: Map<Long, UserModel>
    ): PaymentMethodCashModel {
        val fmt = createDateTimeFormat()
        return PaymentMethodCashModel(
            id = entity.id,
            collectedBy = entity.collectedById?.let { id -> users[id] },
            collectedAt = entity.collectedAt,
            collectedAtText = entity.collectedAt?.let { date -> fmt.format(date) },
        )
    }
}
