package com.wutsi.koki.portal.user.page.settings.invitation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.CreateInvitationRequest
import com.wutsi.koki.tenant.dto.CreateInvitationResponse
import com.wutsi.koki.tenant.dto.InvitationType
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsCreateInvitationControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/settings/invitations/create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_INVITATION_CREATE)

        input("#displayName", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        click("button[type=submit]")

        val request = argumentCaptor<CreateInvitationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/invitations"),
            request.capture(),
            eq(CreateInvitationResponse::class.java),
        )

        assertEquals("Ray Sponsible", request.firstValue.displayName)
        assertEquals("ray.sponsible@gmail.com", request.firstValue.email)
        assertEquals(InvitationType.AGENT, request.firstValue.type)
        assertEquals("fr", request.firstValue.language)

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_INVITATION_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/invitations"),
            any(),
            eq(CreateInvitationResponse::class.java),
        )

        navigateTo("/settings/invitations/create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_INVITATION_CREATE)

        input("#displayName", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        click("button[type=submit]")

        assertElementPresent(".alert-danger")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_INVITATION_CREATE)
    }

    @Test
    fun back() {
        navigateTo("/settings/invitations/create")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_INVITATION_LIST)
    }

    @Test
    fun `create - without permission security-admin`() {
        setupUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/invitations/create")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
