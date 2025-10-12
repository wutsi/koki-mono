package com.wutsi.koki.portal.pub.common.mapper

import com.wutsi.koki.portal.pub.common.model.MoneyModel
import com.wutsi.koki.refdata.dto.Money
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.text.NumberFormat
import kotlin.collections.forEach
import kotlin.let

@Service
class MoneyMapper : TenantAwareMapper() {
    fun toMoneyModel(money: Money): MoneyModel {
        return toMoneyModel(money.amount, money.currency)
    }

    fun toMoneyModel(amount: Double, currency: String?): MoneyModel {
        val xcurrency = currency ?: currentTenant.get()?.currency
        val text = xcurrency?.let { getCurrencyFormatter(xcurrency).format(amount) } ?: amount.toString()
        return MoneyModel(
            value = amount,
            currency = xcurrency ?: "",
            text = text,
            displayText = text,
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
