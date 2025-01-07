package com.wutsi.koki.portal.file.model

import com.wutsi.koki.portal.model.UserModel
import java.net.URLEncoder
import java.util.Date

data class FileModel(
    val id: Long = -1,
    val workflowInstanceId: String? = null,
    val formId: String? = null,
    val name: String = "",
    val contentUrl: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val contentLengthText: String = "",
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdBy: UserModel? = null,
    val extension: String = "",
) {
    val url: String
        get() = "/files/$id/" + URLEncoder.encode(name, "utf-8")
}
