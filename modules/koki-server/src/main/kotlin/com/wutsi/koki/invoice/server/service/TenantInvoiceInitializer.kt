package com.wutsi.koki.invoice.server.service

import com.wutsi.koki.email.server.service.TenantEmailInitializer
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.AbstractTenantModuleInitializer
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class TenantInvoiceInitializer : AbstractTenantModuleInitializer() {
    companion object {
        const val INVOICE_BODY = "/invoice/email/default/invoice.html"
        const val RECEIPT_BODY = "/invoice/email/default/receipt.html"
    }

    override fun init(tenantId: Long) {
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_ENABLED, value = "1", tenantId = tenantId
        )
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_SUBJECT,
            value = InvoiceNotificationWorker.INVOICE_EMAIL_OPENED_SUBJECT,
            tenantId = tenantId
        )
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_BODY, value = IOUtils.toString(
                TenantEmailInitializer::class.java.getResourceAsStream(INVOICE_BODY), "utf-8"
            ), tenantId = tenantId
        )

        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_RECEIPT_ENABLED, value = "1", tenantId = tenantId
        )
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_RECEIPT_SUBJECT,
            value = InvoiceNotificationWorker.INVOICE_EMAIL_PAID_SUBJECT,
            tenantId = tenantId
        )
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_RECEIPT_BODY, value = IOUtils.toString(
                TenantEmailInitializer::class.java.getResourceAsStream(RECEIPT_BODY), "utf-8"
            ), tenantId = tenantId
        )
    }
}
