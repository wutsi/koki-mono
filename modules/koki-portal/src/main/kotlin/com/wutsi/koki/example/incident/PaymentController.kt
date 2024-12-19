package com.wutsi.koki.example.incident

import com.wutsi.koki.sdk.KokiWorkflowInstance
import com.wutsi.koki.workflow.dto.ReceiveExternalEventRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

@RestController
@RequestMapping
class PaymentController(
    private val koki: KokiWorkflowInstance,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaymentController::class.java)
    }

    @PostMapping("/payments")
    fun pay(
        @RequestHeader(name = "X-Workflow-Instance-ID") workflowInstanceId: String,
        @RequestBody request: PaymentRequest
    ) {
        val transactionId = UUID.randomUUID().toString()
        LOGGER.info("amount=${request.amount} employee=${request.employee} email=${request.email} transaction_id=$transactionId")
        koki.received(
            workflowInstanceId = workflowInstanceId,
            request = ReceiveExternalEventRequest(
                name = "payment-successful",
                data = mapOf(
                    "transaction_id" to transactionId,
                    "transaction_date" to SimpleDateFormat("yyyy-MM-dd").format(Date()),
                    "gateway" to "STRIPE",
                )
            )
        )
    }
}

data class PaymentRequest(
    val amount: Double = 0.0,
    val employee: String = "",
    val email: String = ""
)
