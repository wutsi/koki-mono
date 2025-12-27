package com.wutsi.koki.portal.pub.common.util

import com.wutsi.koki.platform.util.NumberUtils
import com.wutsi.koki.portal.pub.common.model.MoneyModel

object MoneyUtil {
    fun priceRangeText(min: MoneyModel, max: MoneyModel): String {
        if (min.amount == max.amount) {
            return min.shortText
        } else {
            val currency = min.shortText.split(" ").firstOrNull() ?: ""
            return currency + " " +
                NumberUtils.shortText(min.amount.toLong()) + " - " +
                NumberUtils.shortText(max.amount.toLong())
        }
    }
}
