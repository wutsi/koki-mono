package com.wutsi.koki.portal.user.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.agent.dto.GenerateQrCodeResponse
import com.wutsi.koki.agent.dto.GetAgentResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.AgentFixtures.agent
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class UserQrCodeControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        // WHEN
        navigateTo("/users/qr-code")

        // THEN
        assertCurrentPageIs(PageName.USER_QR_CODE)
        assertElementAttribute("img.qr-code", "src", agent.qrCodeUrl)
        assertElementPresent("#btn-download-qr-code")
        assertElementNotPresent("#btn-generate-qr-code")
    }

    @Test
    fun `show - agent has no qr code`() {
        // GIVEN
        doReturn(
            ResponseEntity(
                GetAgentResponse(agent.copy(qrCodeUrl = null)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetAgentResponse::class.java)
            )

        // WHEN
        navigateTo("/users/qr-code")

        // THEN
        assertCurrentPageIs(PageName.USER_QR_CODE)
        assertElementNotPresent("img.qr-code")
        assertElementNotPresent("#btn-download-qr-code")
        assertElementPresent("#btn-generate-qr-code")

        click("#btn-generate-qr-code")
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/agents/${agent.id}/qr-code"),
            anyOrNull(),
            eq(GenerateQrCodeResponse::class.java),
        )
        assertCurrentPageIs(PageName.USER_QR_CODE)
    }
}
