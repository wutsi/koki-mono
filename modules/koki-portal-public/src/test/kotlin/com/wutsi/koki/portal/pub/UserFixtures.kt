package com.wutsi.koki.portal.pub

import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary

object UserFixtures {
    val USER_ID = 1111L

    val user = User(
        id = USER_ID,
        email = "ray.sponsible@gmail.com",
        displayName = "Ray Sponsible",
        language = "fr",
        employer = "REIMAX Inc.",
        photoUrl = "https://picsum.photos/1005/200",
    )

    val users = listOf(
        UserSummary(
            id = USER_ID,
            displayName = "Ray Sponsible",
            photoUrl = "https://picsum.photos/1005/200",
            employer = "REIMAX Inc.",
        ),
        UserSummary(
            id = 12L,
            displayName = "Roger Milla",
            photoUrl = "https://picsum.photos/1005/200",
            employer = "REIMAX Inc.",
        ),
        UserSummary(
            id = 13L,
            displayName = "Omam Mbiyick",
            photoUrl = "https://picsum.photos/1005/200",
            employer = "REIMAX Inc.",
        ),
        UserSummary(
            id = 14L,
            displayName = "Roger Milla",
            photoUrl = "https://picsum.photos/1005/200",
            employer = "REIMAX Inc.",
        ),
        UserSummary(
            id = 15L,
            displayName = "Thomas Nkono",
            photoUrl = "https://picsum.photos/1005/200",
            employer = "REIMAX Inc.",
        ),
    )
}
