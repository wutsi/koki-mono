package com.wutsi.koki.payment.server.endpoint

import com.wutsi.koki.payment.dto.CreateCashPaymentRequest
import com.wutsi.koki.payment.dto.CreateCheckPaymentRequest
import com.wutsi.koki.payment.dto.CreateInteractPaymentRequest
import com.wutsi.koki.payment.dto.CreatePaymentResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/payments")
class PaymentEndpoints {
    @PostMapping("/cash")
    fun cash(@Valid @RequestBody request: CreateCashPaymentRequest): CreatePaymentResponse {
        TODO()
    }

    @PostMapping("/interact")
    fun interact(@Valid @RequestBody request: CreateInteractPaymentRequest): CreatePaymentResponse {
        TODO()
    }

    @PostMapping("/check")
    fun check(@Valid @RequestBody request: CreateCheckPaymentRequest): CreatePaymentResponse {
        TODO()
    }
}
