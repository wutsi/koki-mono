package com.wutsi.koki.portal.signup.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures.image
import com.wutsi.koki.RefDataFixtures.categories
import com.wutsi.koki.RefDataFixtures.locations
import com.wutsi.koki.TenantFixtures.config
import com.wutsi.koki.UserFixtures.user
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.CreateUserResponse
import com.wutsi.koki.tenant.dto.SetUserPhotoRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.File
import java.io.FileOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class SignupControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            ResponseEntity(
                GetFileResponse(image),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetFileResponse::class.java)
            )

        setUpAnonymousUser()
    }

    @Test
    fun create() {
        navigateTo("/signup")

        // Index
        assertCurrentPageIs(PageName.SIGNUP)
        input("#name", "Yo Man")
        input("#username", "yoman")
        input("#password", "seCret123")
        input("#confirm-password", "seCret123")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<CreateUserRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users"),
            request.capture(),
            eq(CreateUserResponse::class.java),
        )
        assertEquals("Yo Man", request.firstValue.displayName)
        assertEquals("yoman", request.firstValue.username)
        assertEquals("seCret123", request.firstValue.password)
        assertEquals(null, request.firstValue.language)
        assertEquals(
            listOf(config[ConfigurationName.PORTAL_SIGNUP_ROLE_ID]?.toLong()),
            request.firstValue.roleIds
        )

        // Profile
        assertCurrentPageIs(PageName.SIGNUP_PROFILE)
        input("#name", "Roger Milla")
        input("#email", "roger.milla@gmail.com")
        input("#mobile", "5147580000")
        select("#categoryId", 1)
        input("#employer", "REIMAX 1")
        select2("#country", "Canada")
        select2("#cityId", "${locations[3].name}, ${locations[0].name}")
        scrollToBottom()
        click("button[type=submit]")

        val request2 = argumentCaptor<UpdateUserRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/${user.id}"),
            request2.capture(),
            eq(Any::class.java),
        )
        assertEquals("Roger Milla", request2.firstValue.displayName)
        assertEquals("roger.milla@gmail.com", request2.firstValue.email)
        assertEquals(categories[0].id, request2.firstValue.categoryId)
        assertEquals("REIMAX 1", request2.firstValue.employer)
        assertEquals("+15147580000", request2.firstValue.mobile)
        assertEquals("CA", request2.firstValue.country)
        assertEquals(locations[3].id, request2.firstValue.cityId)

        assertEquals(user.language, request2.firstValue.language)
        assertEquals(user.roleIds, request2.firstValue.roleIds)

        // Photo
        assertCurrentPageIs(PageName.SIGNUP_PHOTO)
        input("#file-upload", getImageFile().absolutePath)
        Thread.sleep(5000)
        click("button[type=submit]")

        val request3 = argumentCaptor<SetUserPhotoRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/${user.id}/photo"),
            request3.capture(),
            eq(Any::class.java),
        )
        assertEquals(image.url, request3.firstValue.photoUrl)

        // Done
        assertCurrentPageIs(PageName.SIGNUP_DONE)
        click("#btn-next")

        assertCurrentPageIs(PageName.LOGIN)
    }

    private fun getImageFile(): File {
        val file = File.createTempFile("photo", ".png")
        file.deleteOnExit()

        val input = this::class.java.getResourceAsStream("/photo.png")
        val output = FileOutputStream(file)
        output.use {
            IOUtils.copy(input, output)
        }
        return file
    }
}
