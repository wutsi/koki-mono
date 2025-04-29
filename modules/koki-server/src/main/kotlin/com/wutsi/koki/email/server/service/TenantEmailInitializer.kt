package com.wutsi.koki.email.server.service

import com.wutsi.koki.platform.messaging.smtp.SMTPType
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.AbstractTenantModuleInitializer
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class TenantEmailInitializer : AbstractTenantModuleInitializer() {
    override fun init(tenantId: Long) {
        setConfigurationIfMissing(
            name = ConfigurationName.EMAIL_DECORATOR,
            value = IOUtils.toString(
                TenantEmailInitializer::class.java.getResourceAsStream("/email/decorator.html"),
                "utf-8"
            ),
            tenantId = tenantId
        )

        setConfigurationIfMissing(
            name = ConfigurationName.SMTP_TYPE,
            value = SMTPType.KOKI.name,
            tenantId = tenantId
        )
    }
}
