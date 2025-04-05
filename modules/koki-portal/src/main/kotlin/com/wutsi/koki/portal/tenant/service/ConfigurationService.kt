package com.wutsi.koki.portal.tenant.service

import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.portal.email.model.EmailDecoratorForm
import com.wutsi.koki.portal.email.model.SMTPForm
import com.wutsi.koki.portal.invoice.form.InvoiceNotificationForm
import com.wutsi.koki.portal.invoice.form.InvoiceSettingsForm
import com.wutsi.koki.portal.payment.form.PaymentNotificationForm
import com.wutsi.koki.portal.payment.form.PaymentSettingsCashForm
import com.wutsi.koki.portal.payment.form.PaymentSettingsCheckForm
import com.wutsi.koki.portal.payment.form.PaymentSettingsCreditCardForm
import com.wutsi.koki.portal.payment.form.PaymentSettingsInteracForm
import com.wutsi.koki.portal.payment.form.PaymentSettingsMobileForm
import com.wutsi.koki.portal.payment.form.PaymentSettingsPaypalForm
import com.wutsi.koki.portal.tax.form.TaxNotificationForm
import com.wutsi.koki.portal.tax.form.TaxNotificationType
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

    fun save(form: InvoiceNotificationForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.INVOICE_EMAIL_ENABLED to "1",
                    ConfigurationName.INVOICE_EMAIL_SUBJECT to (form.subject ?: ""),
                    ConfigurationName.INVOICE_EMAIL_BODY to (form.body ?: ""),
                )
            )
        )
    }

    fun save(form: PaymentNotificationForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.PAYMENT_EMAIL_ENABLED to "1",
                    ConfigurationName.PAYMENT_EMAIL_SUBJECT to (form.subject ?: ""),
                    ConfigurationName.PAYMENT_EMAIL_BODY to (form.body ?: ""),
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

    fun enable(type: TaxNotificationType, status: Boolean) {
        enable(
            name = when (type) {
                TaxNotificationType.assignee -> ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED
                TaxNotificationType.document -> ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED
                TaxNotificationType.done -> ConfigurationName.TAX_EMAIL_DONE_ENABLED
            },
            status = status
        )
    }

    fun enable(type: PaymentMethodType, status: Boolean) {
        val name = when (type) {
            PaymentMethodType.CASH -> ConfigurationName.PAYMENT_METHOD_CASH_ENABLED
            PaymentMethodType.CHECK -> ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED
            PaymentMethodType.INTERAC -> ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED
            PaymentMethodType.PAYPAL -> ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED
            PaymentMethodType.CREDIT_CARD -> ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED
            PaymentMethodType.BANK -> ConfigurationName.PAYMENT_METHOD_BANK_ENABLED
            PaymentMethodType.MOBILE -> ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED
            else -> return
        }
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(name to (if (status) "1" else ""))
            )
        )
    }

    fun save(form: PaymentSettingsCreditCardForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED to "1",
                    ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY to (form.gateway?.name ?: ""),
                    ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY to (form.stripeApiKey ?: ""),
                    ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_ENABLED to if (form.offline) "1" else "",
                    ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_PHONE_NUMBER to if (form.offline) {
                        (form.offlinePhoneNumber ?: "")
                    } else {
                        ""
                    },
                )
            )
        )
    }

    fun save(form: PaymentSettingsPaypalForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED to "1",
                    ConfigurationName.PAYMENT_METHOD_PAYPAL_CLIENT_ID to (form.clientId ?: ""),
                    ConfigurationName.PAYMENT_METHOD_PAYPAL_SECRET_KEY to (form.secretKey ?: ""),
                )
            )
        )
    }

    fun save(form: PaymentSettingsCashForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.PAYMENT_METHOD_CASH_ENABLED to "1",
                    ConfigurationName.PAYMENT_METHOD_CASH_INSTRUCTIONS to (form.instructions?.trim() ?: ""),
                )
            )
        )
    }

    fun save(form: PaymentSettingsCheckForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED to "1",
                    ConfigurationName.PAYMENT_METHOD_CHECK_PAYEE to (form.payee?.trim()?.uppercase() ?: ""),
                    ConfigurationName.PAYMENT_METHOD_CHECK_INSTRUCTIONS to (form.instructions?.trim() ?: ""),
                )
            )
        )
    }

    fun save(form: PaymentSettingsInteracForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED to "1",
                    ConfigurationName.PAYMENT_METHOD_INTERAC_EMAIL to (form.email ?: ""),
                    ConfigurationName.PAYMENT_METHOD_INTERAC_QUESTION to (form.question ?: ""),
                    ConfigurationName.PAYMENT_METHOD_INTERAC_ANSWER to (form.answer ?: ""),
                )
            )
        )
    }

    fun save(form: PaymentSettingsMobileForm) {
        koki.save(
            SaveConfigurationRequest(
                values = mapOf(
                    ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED to "1",
                    ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY to (form.gateway?.name ?: ""),
                    ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY_FLUTTERWAVE_SECRET_KEY to
                        (form.flutterwaveSecretKey ?: ""),
                    ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ENABLED to if (form.offline) "1" else "",
                    ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PHONE_NUMBER to (form.offlinePhoneNumber ?: ""),
                    ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ACCOUNT_NAME to (form.offlineAccountName ?: ""),
                    ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PROVIDER to (form.offlineProvider ?: ""),
                )
            )
        )
    }

    fun save(form: TaxNotificationForm) {
        koki.save(
            SaveConfigurationRequest(
                values = when (form.type) {
                    TaxNotificationType.done -> mapOf(
                        ConfigurationName.TAX_EMAIL_DONE_ENABLED to "1",
                        ConfigurationName.TAX_EMAIL_DONE_SUBJECT to (form.subject ?: ""),
                        ConfigurationName.TAX_EMAIL_DONE_BODY to (form.body ?: ""),
                    )

                    TaxNotificationType.assignee -> mapOf(
                        ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED to "1",
                        ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT to (form.subject ?: ""),
                        ConfigurationName.TAX_EMAIL_ASSIGNEE_BODY to (form.body ?: ""),
                    )

                    TaxNotificationType.document -> mapOf(
                        ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED to "1",
                        ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT to (form.subject ?: ""),
                        ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_BODY to (form.body ?: ""),
                    )

                    else -> emptyMap<String, String>()
                }
            )
        )
    }
}
