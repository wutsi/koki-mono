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
    fun buildViewUrl(returnUrl: String?): String {
        return returnUrl
            ?.let { u -> "$url?return-url=" + URLEncoder.encode(u, "utf-8") }
            ?: url
    }

    fun buildDeleteUrl(returnUrl: String?): String {
        return returnUrl
            ?.let { u -> "$url/delete?return-url=" + URLEncoder.encode(u, "utf-8") }
            ?: "$url/delete"
    }

    val url: String
        get() = "/files/$id"
}
