package com.wutsi.koki.portal.agent.form

data class AgentForm(
    val email: String = "",
    val displayName: String = "",
    val message: String? = null,
)
