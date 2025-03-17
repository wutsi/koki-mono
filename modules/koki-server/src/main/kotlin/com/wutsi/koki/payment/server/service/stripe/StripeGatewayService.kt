package com.wutsi.koki.payment.server.service.stripe

import com.stripe.exception.StripeException
import com.stripe.param.checkout.SessionCreateParams
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.service.InvoiceService
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.payment.server.service.PaymentGatewayException
import com.wutsi.koki.payment.server.service.PaymentGatewayService
import com.wutsi.koki.refdata.server.service.SalesTaxService
import com.wutsi.koki.tenant.server.service.TenantService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.collections.flatMap

@Service
class StripeGatewayService(
    private val stripeClientBuilder: StripeClientBuilder,
    private val invoiceService: InvoiceService,
    private val salesTaxService: SalesTaxService,
    private val tenantService: TenantService,

    @Value("\${koki.payment-gateway.stripe.session.timeout-minutes}") private val timeout: Long
) : PaymentGatewayService {
    @Throws(PaymentGatewayException::class)
    override fun checkout(tx: TransactionEntity) {
        val invoice = invoiceService.get(tx.invoiceId, tx.tenantId)
        val tenant = tenantService.get(tx.tenantId)
        val client = stripeClientBuilder.build(tx.tenantId)
        val redirectUrl = "${tenant.portalUrl}/checkout/confirmation?transaction-id=${tx.id}"

        val params = SessionCreateParams.builder()
            .setSuccessUrl(redirectUrl)
            .setCancelUrl(redirectUrl)
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setExpiresAt(System.currentTimeMillis() + (timeout * 60 * 1000L))
            .setCurrency(tx.currency)
            .putMetadata("tenant_id", tx.tenantId.toString())
            .putMetadata("invoice_id", tx.invoiceId.toString())
            .putMetadata("transaction_id", tx.id)

        invoice.items.forEach { item ->
            params.addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(item.quantity.toLong())
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(item.currency)
                            .setUnitAmount((item.unitPrice * 100.0).toLong())
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.description)
                                    .build()
                            ).build()
                    ).build()
            )
        }

        if (invoice.totalTaxAmount > 0) {
            params.addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(invoice.currency)
                            .setUnitAmount((invoice.totalTaxAmount * 100.0).toLong())
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(taxCodes(invoice))
                                    .build()
                            ).build()
                    ).build()
            )
        }

        try {
            val session = client.checkout().sessions().create(params.build())
            tx.supplierTransactionId = session.id
            tx.supplierStatus = session.status
            tx.checkoutUrl = session.url
        } catch (ex: StripeException) {
            throw PaymentGatewayException(
                errorCode = ErrorCode.TRANSACTION_PAYMENT_FAILED,
                supplierErrorCode = ex.code,
                message = ex.message,
                cause = ex
            )
        }
    }

    private fun taxCodes(invoice: InvoiceEntity): String {
        val ids = invoice.items.flatMap { item -> item.taxes }
            .map { tax -> tax.salesTaxId }
            .distinct()
        val salesTaxes = salesTaxService.search(
            ids = ids,
            limit = ids.size,
        )
        return salesTaxes.map { tax -> tax.name }
            .joinToString(separator = " + ")
    }

    override fun sync(tx: TransactionEntity) {
        try {
            val client = stripeClientBuilder.build(tx.tenantId)
            val session = client.checkout().sessions().retrieve(tx.supplierTransactionId)
            val status = when (session.status) {
                "expired" -> TransactionStatus.FAILED
                "open" -> TransactionStatus.PENDING
                "complete" -> when (session.paymentStatus) {
                    "paid" -> TransactionStatus.SUCCESSFUL
                    else -> tx.status
                }

                else -> tx.status
            }

            if (status != tx.status) {
                tx.status = status
                tx.supplierStatus = session.status
            }
        } catch (ex: StripeException) {
            throw PaymentGatewayException(
                errorCode = ErrorCode.TRANSACTION_PAYMENT_FAILED,
                supplierErrorCode = ex.code,
                message = ex.message,
                cause = ex
            )
        }
    }
}
