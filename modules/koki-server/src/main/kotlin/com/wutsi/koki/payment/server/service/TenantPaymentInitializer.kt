package com.wutsi.koki.payment.server.service

import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.AbstractTenantModuleInitializer
import org.springframework.stereotype.Service

@Service
class TenantPaymentInitializer : AbstractTenantModuleInitializer() {
    override fun init(tenantId: Long) {
        setConfigurationIfMissing(
            name = ConfigurationName.PAYMENT_METHOD_CASH_ENABLED,
            value = "1",
            tenantId = tenantId,
        )
        setConfigurationIfMissing(
            name = ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED,
            value = "1",
            tenantId = tenantId,
        )
    }
}
