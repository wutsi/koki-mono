package com.wutsi.koki

import com.wutsi.koki.RoleFixtures.roles
import com.wutsi.koki.tenant.dto.ProfileStrength
import com.wutsi.koki.tenant.dto.ProfileStrengthBreakdown
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
        websiteUrl = "https://www.linktree.com/ray",
        facebookUrl = "https://www.facebook.com/ray",
        instagramUrl = "https://www.instagram.com/ray",
        youtubeUrl = "https://www.youtube.com/ray",
        tiktokUrl = "https://www.tiktok.com/ray",
        twitterUrl = "https://www.twitter.com/ray",
        biography = """
            Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu.

            In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus.

            Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc,
        """.trimIndent(),
        profileStrength = ProfileStrength(
            value = 85,
            basicInfo = ProfileStrengthBreakdown(value = 15, percentage = 90),
            profilePicture = ProfileStrengthBreakdown(value = 10, percentage = 100),
            socialMedia = ProfileStrengthBreakdown(value = 20, percentage = 60),
            biography = ProfileStrengthBreakdown(value = 15, percentage = 50),
            address = ProfileStrengthBreakdown(value = 15, percentage = 50),
            category = ProfileStrengthBreakdown(value = 10, percentage = 10),
        )
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
            cityId = RefDataFixtures.cities[0].id,
            country = RefDataFixtures.cities[0].country,
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
            cityId = RefDataFixtures.cities[1].id,
            country = RefDataFixtures.cities[1].country,
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
            cityId = RefDataFixtures.cities[2].id,
            country = RefDataFixtures.cities[2].country,
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
