package com.wutsi.koki.portal.model

data class ScriptExecutionModel(
    val errorCode: String? = null,
    val errorMessage: String? = null,
    val bindingsJSON: String? = null,
    val console: String? = null,
)
