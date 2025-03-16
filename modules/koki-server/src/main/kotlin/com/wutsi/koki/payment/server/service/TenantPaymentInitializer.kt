package com.wutsi.koki.payment.server.service

import com.wutsi.koki.email.server.service.TenantEmailInitializer
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.AbstractTenantModuleInitializer
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class TenantPaymentInitializer : AbstractTenantModuleInitializer() {
    companion object {
        const val EMAIL_SUBJECT = "Payment Received – Thank You!"
        const val EMAIL_BODY_PATH = "/payment/email/default/payment.html"
    }

    override fun init(tenantId: Long) {
        setConfigurationIfMissing(
            name = ConfigurationName.PAYMENT_EMAIL_ENABLED,
            value = "1",
            tenantId = tenantId,
        )
        setConfigurationIfMissing(
            name = ConfigurationName.PAYMENT_EMAIL_SUBJECT,
            value = EMAIL_SUBJECT,
            tenantId = tenantId,
        )
        setConfigurationIfMissing(
            name = ConfigurationName.PAYMENT_EMAIL_BODY,
            value = IOUtils.toString(
                TenantEmailInitializer::class.java.getResourceAsStream(EMAIL_BODY_PATH), "utf-8"
            ),
            tenantId = tenantId,
        )
    }
}
