package com.wutsi.koki

import com.wutsi.koki.RoleFixtures.roles
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserSummary

object UserFixtures {
    val USER_ID = 11L

    val user = User(
        id = USER_ID,
        username = "ray.sponsible",
        email = "ray.sponsible@gmail.com",
        displayName = "Ray Sponsible",
        roleIds = listOf(roles[0].id, roles[1].id),
        language = "fr",
        employer = "REIMAX",
        country = "ca",
        categoryId = RefDataFixtures.categories[0].id,
        cityId = RefDataFixtures.cities[0].id,
        mobile = "+15147580000",
        status = UserStatus.ACTIVE,
        photoUrl = "https://picsum.photos/800/600",
        biography = "Hello world",
        websiteUrl = "https://www.linktree.com/ray",
        facebookUrl = "https://www.facebook.com/ray",
        instagramUrl = "https://www.instagram.com/ray",
        youtubeUrl = "https://www.youtube.com/ray",
        tiktokUrl = "https://www.tiktok.com/ray",
        twitterUrl = "https://www.twitter.com/ray",
    )

    val users = listOf(
        UserSummary(
            id = USER_ID,
            displayName = "Ray Sponsible",
            username = "ray.sponsible",
            email = "ray.sponsible@gmail.com",
            employer = "REIMAX",
            photoUrl = "https://picsum.photos/800/600",
            mobile = "+15147580000",
        ),
        UserSummary(
            id = 12L,
            displayName = "Roger Milla",
            username = "roger.milla",
            email = "roger.milla@gmail.com",
            status = UserStatus.ACTIVE,
            employer = "REALTOR",
            photoUrl = "https://picsum.photos/800/600",
            mobile = "+15147580011",
        ),
        UserSummary(
            id = 13L,
            displayName = "Omam Mbiyick",
            username = "ombiyick",
            email = "ombiyick@gmail.com",
            status = UserStatus.ACTIVE,
            employer = "IMMO",
            photoUrl = "https://picsum.photos/800/600",
            mobile = "+15147580022",
        ),
        UserSummary(
            id = 14L,
            displayName = "Roger Milla",
            username = "roger.mila",
            email = "roger.milla@gmail.com",
            status = UserStatus.SUSPENDED,
            employer = null,
            photoUrl = "https://picsum.photos/800/600",
            mobile = null,
        ),
        UserSummary(
            id = 15L,
            displayName = "Thomas Nkono",
            username = "tnkono",
            email = "tnkono@gmail.com",
            status = UserStatus.SUSPENDED,
        ),
    )
}
