package com.wutsi.koki.portal.message.model

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class ConversationModel(
    val id: String = "",
    val ownerId: Long? = null,
    val ownerType: ObjectType? = null,
    val viewed: Boolean = true,
    val lastMessage: MessageModel = MessageModel(),
    val interlocutor: UserModel = UserModel(),
    val modifiedAt: Date = Date(),
    val modifiedAtMoment: String = "",
    val totalUnreadMessages: Int = 0,
    val url: String = "",
) {
    val totalUnreadMessagesText: String?
        get() = if (totalUnreadMessages == 0) {
            null
        } else if (totalUnreadMessages > 9) {
            "9+"
        } else {
            totalUnreadMessages.toString()
        }
}
