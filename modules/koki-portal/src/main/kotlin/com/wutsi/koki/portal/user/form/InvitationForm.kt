package com.wutsi.koki.portal.user.model

import com.wutsi.koki.tenant.dto.InvitationType

data class InvitationForm(
    val displayName: String = "",
    val email: String = "",
    val type: InvitationType? = null,
)
