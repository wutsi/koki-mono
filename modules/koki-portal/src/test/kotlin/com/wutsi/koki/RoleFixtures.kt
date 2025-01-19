package com.wutsi.koki

import com.wutsi.koki.ModuleFixtures.permissions
import com.wutsi.koki.tenant.dto.Role

object RoleFixtures {
    val roles = listOf(
        Role(
            id = 1L,
            name = "accountant",
            title = "Accountant",
            description = "This is an example of role",
            active = true,
            permissionIds = listOf(
                permissions[0].id,
                permissions[1].id,
                permissions[2].id,
                permissions[3].id,
                permissions[4].id,
                permissions[5].id,
            )
        ),
        Role(id = 2L, name = "hr", title = "Human Resource", active = true),
        Role(id = 3L, name = "client", title = "Client", active = false),
    )

    val role = roles[0]
}
