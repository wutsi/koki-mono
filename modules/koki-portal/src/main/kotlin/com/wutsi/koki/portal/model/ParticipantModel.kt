package com.wutsi.koki.portal.model

data class ParticipantModel(
    val role: RoleModel = RoleModel(),
    val user: UserModel = UserModel(),
)
