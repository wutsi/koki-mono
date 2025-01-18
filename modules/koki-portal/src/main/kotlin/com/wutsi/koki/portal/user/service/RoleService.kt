package com.wutsi.koki.portal.user.service

import com.wutsi.koki.portal.user.mapper.UserMapper
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.sdk.KokiUsers
import org.springframework.stereotype.Service

@Service
class RoleService(
    private val koki: KokiUsers,
    private val mapper: UserMapper,
) {
    fun role(id: Long): RoleModel {
        return roles(
            ids = listOf(id)
        ).first()
    }

    fun roles(
        ids: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<RoleModel> {
        val roles = koki.roles(
            ids = ids,
            limit = limit,
            offset = offset
        ).roles

        return roles.map { role -> mapper.toRoleModel(role) }
    }
}
