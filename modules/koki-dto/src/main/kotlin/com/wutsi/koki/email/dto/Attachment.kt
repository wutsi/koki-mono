package com.wutsi.koki.email.dto

data class Attachment(
    val id: Long,
    val type: AttachmentType = AttachmentType.FILE
)
