package com.wutsi.koki.portal.model

import java.net.URLEncoder
import java.util.Date

data class FileModel(
    val id: String = "",
    val workflowInstanceId: String? = null,
    val formId: String? = null,
    val name: String = "",
    val contentUrl: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val contentLengthText: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val createdBy: UserModel? = null,
    val extension: String = "",
) {
    val url: String
        get() = "/files/$id/" + URLEncoder.encode(name, "utf-8")
}
