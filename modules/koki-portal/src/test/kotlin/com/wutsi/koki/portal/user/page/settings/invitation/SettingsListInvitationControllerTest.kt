package com.wutsi.koki.portal.user.page.settings.invitation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.InvitationFixtures.invitation
import com.wutsi.koki.InvitationFixtures.invitations
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.InvitationSummary
import com.wutsi.koki.tenant.dto.SearchInvitationResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID
import kotlin.test.Test

class SettingsListInvitationControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/invitations")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_INVITATION_LIST)

        assertElementCount("tr.invitation", invitations.size)
    }

    @Test
    fun more() {
        var entries = mutableListOf<InvitationSummary>()
        repeat(20) {
            entries.add(invitations[0].copy(id = UUID.randomUUID().toString()))
        }
        doReturn(
            ResponseEntity(
                SearchInvitationResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchInvitationResponse::class.java)
            )

        navigateTo("/settings/invitations")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_INVITATION_LIST)
        assertElementCount("tr.invitation", entries.size)

        scrollToBottom()
        click("#invitation-load-more button", 1000)
        assertElementCount("tr.invitation", 2 * entries.size)
    }

    @Test
    fun create() {
        navigateTo("/settings/invitations")
        click(".btn-create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_INVITATION_CREATE)
    }

    @Test
    fun back() {
        navigateTo("/settings/invitations")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS)
    }

    @Test
    fun `list - without permission security-admin`() {
        setupUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/invitations")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun delete() {
        navigateTo("/settings/invitations")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_INVITATION_LIST)

        click("#delete-${invitation.id}")

        verify(rest).delete(eq("$sdkBaseUrl/v1/invitations/${invitation.id}"))

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_INVITATION_LIST)
        assertElementVisible("#koki-toast")
    }
}
