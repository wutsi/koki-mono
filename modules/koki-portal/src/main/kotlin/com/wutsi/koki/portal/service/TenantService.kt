package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.mapper.TenantMapper
import com.wutsi.koki.portal.page.settings.smtp.SMTPForm
import com.wutsi.koki.sdk.KokiTenants
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.TenantModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TenantService(
    private val koki: KokiTenants,
    private val mapper: TenantMapper,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TenantService::class.java)
    }

    private var all: List<TenantModel>? = null

    fun tenants(): List<TenantModel> {
        if (all == null) {
            all = koki.tenants()
                .tenants
                .map { tenant -> mapper.toTenantModel(tenant) }
            LOGGER.info("${all?.size} Tenant(s) loaded")
        }
        return all!!
    }

    fun configurations(
        names: List<String> = emptyList(),
        keyword: String? = null,
    ): Map<String, String> {
        return koki.configurations(
            names = names,
            keyword = keyword,
        ).configurations
            .map { config -> config.name to config.value }
            .toMap() as Map<String, String>
    }

    fun save(form: SMTPForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.SMTP_PORT to form.port.toString(),
                    ConfigurationName.SMTP_HOST to form.host,
                    ConfigurationName.SMTP_USERNAME to form.username,
                    ConfigurationName.SMTP_PASSWORD to form.password,
                    ConfigurationName.SMTP_FROM_ADDRESS to form.fromAddress,
                    ConfigurationName.SMTP_FROM_PERSONAL to form.fromPersonal,
                )
            )
        )
    }
}
