package com.wutsi.koki.portal.payment.service

import com.stripe.StripeClient
import com.stripe.exception.StripeException
import org.springframework.stereotype.Service

@Service
class StripeValidator {
    @Throws(StripeException::class)
    fun validate(apiKey: String) {
        StripeClient(apiKey).balance().retrieve()
    }
}
