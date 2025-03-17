package com.wutsi.koki.portal.common.mapper

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import org.springframework.stereotype.Service
import java.text.NumberFormat

@Service
class MoneyMapper : TenantAwareMapper() {
    fun toMoneyModel(amount: Double): MoneyModel {
        return MoneyModel(
            value = amount,
            currency = currentTenant.get()?.currency ?: "",
            text = createMoneyFormat().format(amount)
        )
    }

    fun toMoneyModel(amount: Double, currency: String): MoneyModel {
        return MoneyModel(
            value = amount,
            currency = currentTenant.get()?.currency ?: "",
            text = getCurrencyFormatter(currency).format(amount)
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
