package com.wutsi.koki.payment.server.endpoint

import com.wutsi.koki.payment.dto.CreateCashPaymentRequest
import com.wutsi.koki.payment.dto.CreateCheckPaymentRequest
import com.wutsi.koki.payment.dto.CreateInteracPaymentRequest
import com.wutsi.koki.payment.dto.CreatePaymentResponse
import com.wutsi.koki.payment.dto.PrepareCheckoutRequest
import com.wutsi.koki.payment.dto.PrepareCheckoutResponse
import com.wutsi.koki.payment.dto.event.TransactionCompletedEvent
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.payment.server.service.PaymentService
import com.wutsi.koki.platform.mq.Publisher
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/payments")
class PaymentEndpoints(
    private val service: PaymentService,
    private val publisher: Publisher,
) {
    @PostMapping("/cash")
    fun cash(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateCashPaymentRequest,
    ): CreatePaymentResponse {
        val tx = service.cash(request, tenantId)
        publish(tx)
        return CreatePaymentResponse(
            transactionId = tx.id!!,
            status = tx.status,
        )
    }

    @PostMapping("/interac")
    fun interact(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateInteracPaymentRequest,
    ): CreatePaymentResponse {
        val tx = service.interact(request, tenantId)
        publish(tx)
        return CreatePaymentResponse(
            transactionId = tx.id!!,
            status = tx.status,
        )
    }

    @PostMapping("/check")
    fun check(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateCheckPaymentRequest,
    ): CreatePaymentResponse {
        val tx = service.check(request, tenantId)
        publish(tx)
        return CreatePaymentResponse(
            transactionId = tx.id!!,
            status = tx.status,
        )
    }

    @PostMapping("/checkout")
    fun checkout(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: PrepareCheckoutRequest,
    ): PrepareCheckoutResponse {
        val tx = service.checkout(request, tenantId)
        return PrepareCheckoutResponse(
            transactionId = tx.id!!,
            status = tx.status,
            redirectUrl = tx.checkoutUrl!!,
        )
    }

    private fun publish(tx: TransactionEntity) {
        publisher.publish(
            TransactionCompletedEvent(
                transactionId = tx.id!!,
                tenantId = tx.tenantId,
                status = tx.status,
            )
        )
    }
}
