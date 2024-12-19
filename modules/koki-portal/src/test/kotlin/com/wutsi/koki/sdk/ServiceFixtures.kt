package com.wutsi.koki

import com.wutsi.koki.service.dto.AuthorizationType
import com.wutsi.koki.service.dto.Service
import com.wutsi.koki.service.dto.ServiceSummary

object ServiceFixtures {
    val SERVICE_ID = "11111-22222-33333"
    val SERVICE_NAME = "SRV-001"

    val service = Service(
        id = SERVICE_ID,
        name = SERVICE_NAME,
        title = "Process Payment",
        description = "Process payment using STRIPE",
        active = true,
        authorizationType = AuthorizationType.BASIC,
        username = "stripe-usr",
        password = "111111",
        apiKey = "543905493-59409540-905490",
        baseUrl = "https://api.stripe.com",
    )

    val services = listOf(
        ServiceSummary(
            id = SERVICE_ID,
            name = SERVICE_NAME,
            title = "Generate Incident ID",
            active = true,
            baseUrl = "https://api.stripe.com",
        ),
        ServiceSummary(
            id = "2",
            name = "SRV-002",
            title = "This is the 2nd service",
            active = false,
            baseUrl = "https://api.paypal.com",
        ),
        ServiceSummary(
            id = "3",
            name = "SCR-003",
            title = "This is the 3rd service",
            active = true,
            baseUrl = "https://api.flutterwave.com",
        ),
    )
}
