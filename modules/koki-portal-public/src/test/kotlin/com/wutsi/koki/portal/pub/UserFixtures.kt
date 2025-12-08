package com.wutsi.koki.portal.pub

import com.wutsi.koki.portal.pub.RefDataFixtures.cities
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
        mobile = "+15147580000",
        cityId = cities[0].id,
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
        """.trimIndent()
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
