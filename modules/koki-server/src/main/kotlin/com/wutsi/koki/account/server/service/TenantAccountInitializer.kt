package com.wutsi.koki.account.server.service

import com.wutsi.koki.email.server.service.TenantEmailInitializer
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.AbstractTenantModuleInitializer
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class TenantAccountInitializer : AbstractTenantModuleInitializer() {
    companion object {
        const val EMAIL_SUBJECT = "Invitation to join #{{businessName}} Portal"
        const val EMAIL_BODY_PATH = "/account/email/invite.html"
    }

    override fun init(tenantId: Long) {
        setConfigurationIfMissing(
            name = ConfigurationName.ACCOUNT_INVITE_EMAIL_SUBJECT,
            value = EMAIL_SUBJECT,
            tenantId = tenantId,
        )
        setConfigurationIfMissing(
            name = ConfigurationName.ACCOUNT_INVITE_EMAIL_BODY,
            value = IOUtils.toString(
                TenantEmailInitializer::class.java.getResourceAsStream(EMAIL_BODY_PATH), "utf-8"
            ),
            tenantId = tenantId,
        )
    }
}
