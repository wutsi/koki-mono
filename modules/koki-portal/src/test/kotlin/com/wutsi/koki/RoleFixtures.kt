package com.wutsi.koki

import com.wutsi.koki.tenant.dto.Role

object RoleFixtures {
    val roles = listOf(
        Role(id = 1L, name = "accountant", title = "Accountant"),
        Role(id = 2L, name = "hr", title = "Human Resource"),
        Role(id = 3L, name = "client", title = "Client"),
    )
}
