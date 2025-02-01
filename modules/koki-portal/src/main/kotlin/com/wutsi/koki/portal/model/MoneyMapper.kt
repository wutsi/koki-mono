package com.wutsi.koki.portal.model

import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import org.springframework.stereotype.Service

@Service
class MoneyMapper : TenantAwareMapper() {
    fun toMoneyModel(amount: Double): MoneyModel {
        return MoneyModel(
            value = amount,
            currency = currentTenant.get()?.currency ?: "",
            text = createMoneyFormat().format(amount)
        )
    }
}
