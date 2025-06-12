package com.wutsi.koki.portal.message.model

import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import java.util.Date

data class MessageModel(
    val id: Long = -1,
    val senderName: String = "",
    val senderEmail: String = "",
    val senderPhone: String? = null,
    val status: MessageStatus = MessageStatus.UNKNOWN,
    val body: String = "",
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdAtMoment: String = "",
    val country: String? = null,
    val owner: ObjectReferenceModel? = null,
) {
    val archived: Boolean
        get() = status == MessageStatus.ARCHIVED

    val countryFlag: String?
        get() = country?.let { "https://flagcdn.com/w20/${country.lowercase()}.png" }
}
