package com.wutsi.koki.form.event

data class FormUpdatedEvent(
    val formId: String = "",
    val formDataId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
