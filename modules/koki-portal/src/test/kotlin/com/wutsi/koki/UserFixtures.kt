package com.wutsi.koki

import com.wutsi.koki.RoleFixtures.roles
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary

object UserFixtures {
    val USER_ID = 11L

    val user = User(
        id = USER_ID,
        email = "ray.sponsible@gmail.com",
        displayName = "Ray Sponsible",
        roleIds = listOf(roles[0].id),
        language = "fr",
    )

    val users = listOf(
        UserSummary(id = USER_ID, displayName = "Ray Sponsible"),
        UserSummary(id = 12L, displayName = "Roger Milla"),
        UserSummary(id = 13L, displayName = "Omam Mbiyick"),
        UserSummary(id = 14L, displayName = "Roger Milla"),
        UserSummary(id = 15L, displayName = "Thomas Nkono"),
    )
}
