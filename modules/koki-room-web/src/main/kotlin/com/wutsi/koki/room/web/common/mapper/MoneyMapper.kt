package com.wutsi.koki.room.web.common.mapper

import com.wutsi.koki.room.web.common.model.MoneyModel
import org.springframework.stereotype.Service
import java.text.NumberFormat

@Service
class MoneyMapper : TenantAwareMapper() {
    fun toMoneyModel(amount: Double, currency: String?): MoneyModel {
        val xcurrency = currency ?: currentTenant.get()?.currency
        return MoneyModel(
            value = amount,
            currency = xcurrency ?: "",
            text = xcurrency?.let { getCurrencyFormatter(xcurrency).format(amount) } ?: amount.toString()
        )
    }

    private fun getCurrencyFormatter(currency: String): NumberFormat {
        if (currentTenant.get()?.currency == currency) {
            return createMoneyFormat()
        }

        NumberFormat.getAvailableLocales().forEach { locale ->
            val fmt = NumberFormat.getCurrencyInstance(locale)
            if (fmt.getCurrency().getCurrencyCode() == currency) {
                return fmt
            }
        }
        throw IllegalStateException("Currency not supported: $currency")
    }
}
