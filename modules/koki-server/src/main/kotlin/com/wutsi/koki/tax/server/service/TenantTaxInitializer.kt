package com.wutsi.koki.invoice.server.service

import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.AbstractTenantModuleInitializer
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class TenantTaxInitializer : AbstractTenantModuleInitializer() {
    companion object {
        const val EMAIL_ASSIGNEE_SUBJECT = "You have a new task"
        const val EMAIL_ASSIGNEE_BODY_PATH = "/tax/email/default/assignee.html"
    }

    override fun init(tenantId: Long) {
        setConfigurationIfMissing(
            name = ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED,
            value = "1",
            tenantId = tenantId,
        )
        setConfigurationIfMissing(
            name = ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT,
            value = EMAIL_ASSIGNEE_SUBJECT,
            tenantId = tenantId,
        )
        setConfigurationIfMissing(
            name = ConfigurationName.TAX_EMAIL_ASSIGNEE_BODY,
            value = IOUtils.toString(
                TenantTaxInitializer::class.java.getResourceAsStream(EMAIL_ASSIGNEE_BODY_PATH), "utf-8"
            ),
            tenantId = tenantId,
        )
    }
}
