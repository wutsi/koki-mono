package com.wutsi.koki.portal.lead.model

import com.wutsi.koki.platform.util.HtmlUtils
import java.util.Date

data class LeadMessageModel(
    val id: Long = -1,
    val content: String = "",
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val visitRequestedAt: Date? = null,
    val visitRequestedAtText: String? = null,
) {
    val contentHtml: String
        get() = HtmlUtils.toHtml(content)
}
