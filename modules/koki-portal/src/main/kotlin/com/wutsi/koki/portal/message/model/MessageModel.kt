package com.wutsi.koki.portal.message.model

import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class MessageModel(
    val id: Long = -1,
    val sender: UserModel = UserModel(),
    val body: String = "",
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdAtMoment: String = "",

    @Deprecated("") val senderName: String = "",
    @Deprecated("") val senderEmail: String = "",
    @Deprecated("") val senderPhone: String? = null,
    @Deprecated("") val status: MessageStatus = MessageStatus.UNKNOWN,
    @Deprecated("") val country: String? = null,
    @Deprecated("") val owner: ObjectReferenceModel? = null,
) {
    @Deprecated("")
    val archived: Boolean
        get() = status == MessageStatus.ARCHIVED

    @Deprecated("")
    val countryFlag: String?
        get() = country?.let { "https://flagcdn.com/w20/${country.lowercase()}.png" }
}
