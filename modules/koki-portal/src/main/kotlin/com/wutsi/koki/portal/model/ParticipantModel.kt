package com.wutsi.koki.portal.model

import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.model.UserModel

data class ParticipantModel(
    val role: RoleModel = RoleModel(),
    val user: UserModel = UserModel(),
)
