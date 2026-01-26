package com.wutsi.koki.portal.agent.model

import com.wutsi.koki.portal.user.model.UserModel

data class AgentModel(
    val id: Long = -1,
    val qrCodeUrl: String? = null,
    val publicUrl: String = "",
    val user: UserModel = UserModel(),
)
