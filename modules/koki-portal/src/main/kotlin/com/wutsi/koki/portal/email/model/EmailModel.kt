package com.wutsi.koki.portal.email.model

import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class EmailModel(
    val id: String = "",
    val recipient: RecipientModel = RecipientModel(),
    val subject: String = "",
    val body: String = "",
    val summary: String = "",
    val attachmentFileIds: List<Long> = emptyList(),
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdAtMoment: String = "",
    val sender: UserModel = UserModel(),
)
