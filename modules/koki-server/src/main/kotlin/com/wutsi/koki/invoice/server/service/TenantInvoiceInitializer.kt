package com.wutsi.koki.invoice.server.service

import com.wutsi.koki.email.server.service.TenantEmailInitializer
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.AbstractTenantModuleInitializer
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class TenantInvoiceInitializer : AbstractTenantModuleInitializer() {
    override fun init(tenantId: Long) {
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_OPENED_ENABLED,
            value = "1",
            tenantId = tenantId
        )
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_OPENED_SUBJECT,
            value = InvoiceNotificationWorker.INVOICE_EMAIL_OPENED_SUBJECT,
            tenantId = tenantId
        )
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_OPENED_BODY,
            value = IOUtils.toString(
                TenantEmailInitializer::class.java.getResourceAsStream("/invoice/email/opened.html"),
                "utf-8"
            ),
            tenantId = tenantId
        )

        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_PAID_ENABLED,
            value = "1",
            tenantId = tenantId
        )
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_PAID_SUBJECT,
            value = InvoiceNotificationWorker.INVOICE_EMAIL_PAID_SUBJECT,
            tenantId = tenantId
        )
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_PAID_BODY,
            value = IOUtils.toString(
                TenantEmailInitializer::class.java.getResourceAsStream("/invoice/email/paid.html"),
                "utf-8"
            ),
            tenantId = tenantId
        )
    }
}
