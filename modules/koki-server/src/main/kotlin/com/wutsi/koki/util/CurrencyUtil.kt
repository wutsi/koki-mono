package com.wutsi.koki.util

import java.text.NumberFormat

object CurrencyUtil {
    fun getNumberFormat(currency: String): NumberFormat {
        NumberFormat.getAvailableLocales().forEach { locale ->
            val fmt = NumberFormat.getCurrencyInstance(locale)
            if (fmt.getCurrency().getCurrencyCode() == currency) {
                return fmt
            }
        }
        throw IllegalStateException("Currency not supported: $currency")
    }
}
