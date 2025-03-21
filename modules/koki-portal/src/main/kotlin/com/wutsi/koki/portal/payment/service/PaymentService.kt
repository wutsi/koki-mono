package com.wutsi.koki.portal.payment.service

import com.wutsi.koki.payment.dto.CreateCashPaymentRequest
import com.wutsi.koki.payment.dto.CreateCheckPaymentRequest
import com.wutsi.koki.payment.dto.CreateInteracPaymentRequest
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.PrepareCheckoutRequest
import com.wutsi.koki.portal.payment.form.PaymentForm
import com.wutsi.koki.sdk.KokiPayments
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class PaymentService(
    private val koki: KokiPayments,
) {
    fun checkout(
        invoiceId: Long,
        paynowId: String?,
        paymentMethodType: PaymentMethodType
    ): String? {
        return koki.checkout(
            PrepareCheckoutRequest(
                invoiceId = invoiceId,
                paynowId = paynowId,
                paymentMethodType = paymentMethodType
            )
        ).redirectUrl
    }

    fun create(form: PaymentForm): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd")

        val response = when (form.paymentMethodType) {
            PaymentMethodType.CASH -> koki.cashPayment(
                request = CreateCashPaymentRequest(
                    invoiceId = form.invoiceId,
                    amount = form.amount,
                    currency = form.currency,
                    description = form.description,

                    collectedAt = form.collectedAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                    collectedById = form.collectedById,
                )
            )

            PaymentMethodType.CHECK -> koki.checkPayment(
                request = CreateCheckPaymentRequest(
                    invoiceId = form.invoiceId,
                    amount = form.amount,
                    currency = form.currency,
                    description = form.description,

                    checkNumber = form.checkNumber,
                    bankName = form.bankName.uppercase(),
                    checkDate = form.checkDate?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                    clearedAt = form.clearedAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                )
            )

            PaymentMethodType.INTERAC -> koki.interacPayment(
                request = CreateInteracPaymentRequest(
                    invoiceId = form.invoiceId,
                    amount = form.amount,
                    currency = form.currency,
                    description = form.description,

                    referenceNumber = form.referenceNumber,
                    bankName = form.bankName.uppercase(),
                    sentAt = form.sentAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                    clearedAt = form.clearedAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                )
            )

            else -> TODO()
        }

        return response.transactionId
    }
}
