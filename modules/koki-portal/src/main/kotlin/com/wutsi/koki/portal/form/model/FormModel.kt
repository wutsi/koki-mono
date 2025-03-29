package com.wutsi.koki.portal.form.model

import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class FormModel(
    val id: Long = -1,
    val name: String = "",
    var description: String? = null,
    var active: Boolean = true,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdAtMoment: String = "",
    val createdBy: UserModel? = null,
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val modifiedAtMoment: String = "",
    val modifiedBy: UserModel? = null,
)
