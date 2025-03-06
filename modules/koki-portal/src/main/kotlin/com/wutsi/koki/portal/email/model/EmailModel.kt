package com.wutsi.koki.portal.email.model

import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class EmailModel(
    val id: String = "",
    val recipient: RecipientModel = RecipientModel(),
    val subject: String = "",
    val body: String = "",
    val summary: String = "",
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdAtMoment: String = "",
    val sender: UserModel? = null,
    val attachmentFileCount: Int = 0,
    val attachmentFiles: List<FileModel> = emptyList(),
) {
    val hasAttachment: Boolean
        get() = attachmentFileCount > 0
}
