package com.wutsi.koki.portal.tenant.service

import com.wutsi.koki.portal.email.model.EmailDecoratorForm
import com.wutsi.koki.portal.email.model.SMTPForm
import com.wutsi.koki.portal.file.form.StorageForm
import com.wutsi.koki.portal.invoice.form.InvoiceNotificationSettingsForm
import com.wutsi.koki.portal.invoice.form.InvoiceSettingsForm
import com.wutsi.koki.portal.invoice.model.InvoiceNotificationType
import com.wutsi.koki.sdk.KokiConfiguration
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import org.springframework.stereotype.Service

@Service
class ConfigurationService(
    private val koki: KokiConfiguration,
) {
    fun configurations(
        names: List<String> = emptyList(),
        keyword: String? = null,
    ): Map<String, String> {
        return koki.configurations(
            names = names,
            keyword = keyword,
        ).configurations
            .map { config -> config.name to config.value }
            .toMap()
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

    fun save(form: EmailDecoratorForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.EMAIL_DECORATOR to form.content,
                )
            )
        )
    }

    fun save(form: StorageForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.STORAGE_TYPE to form.type,
                    ConfigurationName.STORAGE_S3_BUCKET to form.s3Bucket,
                    ConfigurationName.STORAGE_S3_REGION to form.s3Region,
                    ConfigurationName.STORAGE_S3_SECRET_KEY to form.s3SecretKey,
                    ConfigurationName.STORAGE_S3_ACCESS_KEY to form.s3AccessKey,
                )
            )
        )
    }

    fun save(form: InvoiceSettingsForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.INVOICE_DUE_DAYS to form.dueDays.toString(),
                    ConfigurationName.INVOICE_START_NUMBER to form.startNumber.toString(),
                )
            )
        )
    }

    fun save(form: InvoiceNotificationSettingsForm) {
        koki.save(
            SaveConfigurationRequest(
                values = if (form.type == InvoiceNotificationType.paid) {
                    mapOf(
                        ConfigurationName.INVOICE_EMAIL_PAID_SUBJECT to (form.subject ?: ""),
                        ConfigurationName.INVOICE_EMAIL_PAID_BODY to (form.body ?: ""),
                    )
                } else if (form.type == InvoiceNotificationType.opened) {
                    mapOf(
                        ConfigurationName.INVOICE_EMAIL_OPENED_SUBJECT to (form.subject ?: ""),
                        ConfigurationName.INVOICE_EMAIL_OPENED_BODY to (form.body ?: ""),
                    )
                } else {
                    emptyMap()
                }
            )
        )
    }

    fun enable(type: InvoiceNotificationType, status: Boolean) {
        koki.save(
            SaveConfigurationRequest(
                values = if (type == InvoiceNotificationType.paid) {
                    mapOf(
                        ConfigurationName.INVOICE_EMAIL_PAID_ENABLED to (if (status) "1" else "")
                    )
                } else if (type == InvoiceNotificationType.opened) {
                    mapOf(
                        ConfigurationName.INVOICE_EMAIL_OPENED_ENABLED to (if (status) "1" else "")
                    )
                } else {
                    emptyMap()
                }
            )
        )
    }
}
