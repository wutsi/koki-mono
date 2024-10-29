package org.example.com.wutsi.koki.tenant.dto

data class DocumentFile(
    val id: Long = -1,
    val documentId: Long = -1,
    val file: File = File(),
)
