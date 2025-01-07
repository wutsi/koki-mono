package com.wutsi.koki.portal.model

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
        return if (returnUrl.isNullOrEmpty()) {
            url
        } else {
            "$url?return-url=" + URLEncoder.encode(returnUrl, "utf-8")
        }
    }

    fun buildDeleteUrl(returnUrl: String?): String {
        return if (returnUrl.isNullOrEmpty()) {
            "$url/delete"
        } else {
            "$url/delete?return-url=" + URLEncoder.encode(returnUrl, "utf-8")
        }
    }

    val url: String
        get() = "/files/$id"
}
