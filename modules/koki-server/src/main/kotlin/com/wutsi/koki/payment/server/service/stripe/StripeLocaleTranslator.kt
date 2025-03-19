package com.wutsi.koki.payment.server.service.stripe

import com.stripe.param.checkout.SessionCreateParams
import org.springframework.stereotype.Service

@Service
class StripeLocaleTranslator {
    fun translate(locale: String?): SessionCreateParams.Locale? {
        if (locale == null) return null
        return fromLocale(locale) ?: fromLanguage(locale)
    }

    fun fromLocale(locale: String): SessionCreateParams.Locale? {
        val xlocale = locale.replace("_", "-")
        return SessionCreateParams.Locale.entries.find { entry ->
            entry.value == xlocale
        }
    }

    fun fromLanguage(locale: String): SessionCreateParams.Locale? {
        val part = locale.split("_")
        val language = part[0]
        return SessionCreateParams.Locale.entries.find { entry ->
            entry.value == language
        }
    }
}
