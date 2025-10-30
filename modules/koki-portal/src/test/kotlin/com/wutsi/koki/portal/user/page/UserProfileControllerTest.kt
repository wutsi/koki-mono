package com.wutsi.koki.portal.user.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.RefDataFixtures.categories
import com.wutsi.koki.RefDataFixtures.locations
import com.wutsi.koki.UserFixtures.user
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.UpdateUserProfileRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserProfileControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/users/profile")
        assertCurrentPageIs(PageName.USER_PROFILE)

        input("#displayName", "Yo Man")
        input("#email", "yoman@gmail.com")
        input("#mobile", "5147580111")
        input("#biography", "This is the bio")
        scroll(.33)
        select2("#language", "English")
        select2("#country", "Canada")
        select2("#cityId", "${locations[2].name}, ${locations[0].name}")
        select("#categoryId", 2)
        input("#employer", "LOOKOS")
        input("#facebookUrl", "https://www.facebook.com/yo.man")
        input("#instagramUrl", "https://www.instagram.com/yo.man")
        scroll(.33)
        input("#tiktokUrl", "https://www.tiktok.com/yo.man")
        input("#youtubeUrl", "https://www.youtube.com/yo.man")
        input("#twitterUrl", "https://www.x.com/yo.man")
        input("#websiteUrl", "https://www.linktree.com/yo.man")
        click("button[type=submit]")

        val request = argumentCaptor<UpdateUserProfileRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/${user.id}/profile"),
            request.capture(),
            eq(Any::class.java),
        )

        assertEquals("Yo Man", request.firstValue.displayName)
        assertEquals("yoman@gmail.com", request.firstValue.email)
        assertEquals("en", request.firstValue.language)
        assertEquals("+15147580111", request.firstValue.mobile)
        assertEquals("This is the bio", request.firstValue.biography)
        assertEquals("en", request.firstValue.language)
        assertEquals("CA", request.firstValue.country)
        assertEquals(locations[2].id, request.firstValue.cityId)
        assertEquals(categories[1].id, request.firstValue.categoryId)
        assertEquals("LOOKOS", request.firstValue.employer)
        assertEquals("https://www.facebook.com/yo.man", request.firstValue.facebookUrl)
        assertEquals("https://www.instagram.com/yo.man", request.firstValue.instagramUrl)
        assertEquals("https://www.youtube.com/yo.man", request.firstValue.youtubeUrl)
        assertEquals("https://www.tiktok.com/yo.man", request.firstValue.tiktokUrl)
        assertEquals("https://www.x.com/yo.man", request.firstValue.twitterUrl)
        assertEquals("https://www.linktree.com/yo.man", request.firstValue.websiteUrl)

        assertCurrentPageIs(PageName.HOME)
    }
}
