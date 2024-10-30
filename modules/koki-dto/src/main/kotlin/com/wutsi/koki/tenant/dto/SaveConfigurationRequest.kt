package com.wutsi.koki.tenant.dto

data class SaveConfigurationRequest(
    val values: Map<String, String> = emptyMap()
)
