package com.wutsi.koki.portal.user.service

import com.wutsi.koki.portal.module.service.ModuleService
import com.wutsi.koki.portal.user.mapper.UserMapper
import com.wutsi.koki.portal.user.model.RoleForm
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.sdk.KokiUsers
import com.wutsi.koki.tenant.dto.CreateRoleRequest
import com.wutsi.koki.tenant.dto.UpdateRoleRequest
import org.springframework.stereotype.Service

@Service
class RoleService(
    private val koki: KokiUsers,
    private val mapper: UserMapper,
    private val moduleService: ModuleService,
) {
    fun role(id: Long): RoleModel {
        return roles(
            ids = listOf(id)
        ).first()
    }

    fun roles(
        ids: List<Long> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<RoleModel> {
        // Roles
        val roles = koki.roles(
            ids = ids,
            active = active,
            limit = limit,
            offset = offset
        ).roles

        // permissions
        val permissionIds = roles.flatMap { role -> role.permissionIds }
            .toSet()
        val permissions = if (permissionIds.isEmpty()) {
            emptyMap()
        } else {
            moduleService
                .permissions(permissionIds.toList())
                .associateBy { permission -> permission.id }
        }
        return roles.map { role -> mapper.toRoleModel(role, permissions) }
    }

    fun create(form: RoleForm): Long {
        return koki.createRole(
            CreateRoleRequest(
                name = form.name,
                title = form.title,
                description = form.description,
                active = form.active,
                permissionIds = form.permissionIds,
            )
        ).roleId
    }

    fun update(id: Long, form: RoleForm) {
        koki.updateRole(
            id,
            UpdateRoleRequest(
                name = form.name,
                title = form.title,
                description = form.description,
                active = form.active,
                permissionIds = form.permissionIds,
            )
        )
    }

    fun delete(id: Long) {
        koki.deleteRole(id)
    }
}
