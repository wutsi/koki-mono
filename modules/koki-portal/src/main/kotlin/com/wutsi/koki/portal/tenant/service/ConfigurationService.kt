package com.wutsi.koki.portal.tenant.service

import com.wutsi.koki.portal.email.model.EmailDecoratorForm
import com.wutsi.koki.portal.email.model.SMTPForm
import com.wutsi.koki.sdk.KokiConfiguration
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import org.springframework.stereotype.Service

@Service
class

ConfigurationService(
    private val koki: KokiConfiguration,
) {
    fun configurations(
        names: List<String> = emptyList(),
        keyword: String? = null,
    ): Map<String, String> {
        return koki.configurations(
            names = names,
            keyword = keyword,
        ).configurations.map { config -> config.name to config.value }.toMap()
    }

    fun save(configs: Map<String, String>) {
        koki.save(
            SaveConfigurationRequest(configs)
        )
    }

    fun save(form: SMTPForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.SMTP_TYPE to form.type,
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

    fun save(form: EmailDecoratorForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.EMAIL_DECORATOR to form.content,
                )
            )
        )
    }

    fun enable(name: String, status: Boolean) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(name to (if (status) "1" else ""))
            )
        )
    }
}
