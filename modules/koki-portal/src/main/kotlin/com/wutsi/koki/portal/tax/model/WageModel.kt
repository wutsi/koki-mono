package com.wutsi.koki.portal.tax.model

import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.koki.portal.user.model.UserModel

data class WageModel(
    val user: UserModel = UserModel(),
    val hourly: MoneyModel = MoneyModel()
)
