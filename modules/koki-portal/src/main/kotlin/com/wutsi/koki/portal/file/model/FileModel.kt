package com.wutsi.koki.portal.file.model

import com.wutsi.koki.portal.user.model.UserModel
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
    val createdAtMoment: String = "",
    val createdBy: UserModel? = null,
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val description: String? = null,
    val extension: String = "",
    val labels: List<LabelModel> = emptyList()
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
