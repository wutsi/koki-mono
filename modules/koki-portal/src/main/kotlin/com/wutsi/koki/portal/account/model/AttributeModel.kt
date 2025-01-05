package com.wutsi.koki.portal.account.model

import com.wutsi.koki.account.dto.AttributeType
import java.util.Date

data class AttributeModel(
    val id: Long = -1,
    val name: String = "",
    val type: AttributeType = AttributeType.UNKNOWN,
    val required: Boolean = false,
    val active: Boolean = false,
    val label: String = "",
    val description: String? = null,
    val choices: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
) {
    val htmlInput: Boolean
        get() = type == AttributeType.TEXT ||
            type == AttributeType.DATE ||
            type == AttributeType.TIME ||
            type == AttributeType.EMAIL ||
            type == AttributeType.URL ||
            type == AttributeType.NUMBER ||
            type == AttributeType.DECIMAL

    val htmlInputType: String?
        get() = when (type) {
            AttributeType.DATE -> "date"
            AttributeType.TIME -> "time"
            AttributeType.EMAIL -> "email"
            AttributeType.URL -> "url"
            AttributeType.NUMBER, AttributeType.DECIMAL -> "number"
            else -> null
        }
}
