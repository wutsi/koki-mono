package com.wutsi.koki

import com.wutsi.koki.RoleFixtures.roles
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import com.wutsi.koki.tenant.dto.UserType

object UserFixtures {
    val USER_ID = 11L

    val user = User(
        id = USER_ID,
        email = "ray.sponsible@gmail.com",
        displayName = "Ray Sponsible",
        roleIds = listOf(roles[0].id)
    )

    val users = listOf(
        UserSummary(id = USER_ID, displayName = "Ray Sponsible"),
        UserSummary(id = 12L, displayName = "Roger Milla", type = UserType.EMPLOYEE),
        UserSummary(id = 13L, displayName = "Omam Mbiyick", type = UserType.EMPLOYEE),
        UserSummary(id = 14L, displayName = "Roger Milla"),
        UserSummary(id = 15L, displayName = "Thomas Nkono", type = UserType.EMPLOYEE),
    )
}
