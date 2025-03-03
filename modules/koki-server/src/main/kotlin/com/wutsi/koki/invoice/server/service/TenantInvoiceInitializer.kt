package com.wutsi.koki.invoice.server.service

import com.wutsi.koki.email.server.service.TenantEmailInitializer
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.AbstractTenantModuleInitializer
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class TenantInvoiceInitializer : AbstractTenantModuleInitializer() {
    override fun init(tenatId: Long) {
        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_OPENED,
            value = IOUtils.toString(
                TenantEmailInitializer::class.java.getResourceAsStream("/invoice/email/opened.html"),
                "utf-8"
            ),
            tenantId = tenatId
        )

        setConfigurationIfMissing(
            name = ConfigurationName.INVOICE_EMAIL_PAID,
            value = IOUtils.toString(
                TenantEmailInitializer::class.java.getResourceAsStream("/invoice/email/paid.html"),
                "utf-8"
            ),
            tenantId = tenatId
        )
    }
}
