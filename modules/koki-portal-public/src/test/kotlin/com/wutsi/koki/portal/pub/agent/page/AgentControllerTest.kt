package com.wutsi.koki.portal.pub.agent.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.dto.CreateLeadResponse
import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.platform.geoip.GeoIp
import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.AgentFixtures.agent
import com.wutsi.koki.portal.pub.FileFixtures
import com.wutsi.koki.portal.pub.RefDataFixtures.cities
import com.wutsi.koki.portal.pub.RefDataFixtures.locations
import com.wutsi.koki.portal.pub.TenantFixtures.tenants
import com.wutsi.koki.portal.pub.UserFixtures.user
import com.wutsi.koki.portal.pub.common.page.PageName
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AgentControllerTest : AbstractPageControllerTest() {
    @MockitoBean
    private lateinit var ipService: GeoIpService

    private val geoIp = GeoIp(
        countryCode = "CA",
        city = cities[0].name,
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            ResponseEntity(
                SearchFileResponse(FileFixtures.images),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchFileResponse::class.java)
            )

        doReturn(geoIp).whenever(ipService).resolve(any())
    }

    @Test
    fun show() {
        navigateTo("/agents/${agent.id}")
        assertCurrentPageIs(PageName.AGENT)

        // Meta
        assertElementAttribute("html", "lang", "fr")
        assertElementAttribute(
            "head meta[name='description']",
            "content",
            "Agent immobilier Ray Sponsible de Quebec,Canada. Consultez les listings de l'agent et contactez-le pour tous vos besoins immobiliers."
        )

        // Opengraph
        assertElementAttribute(
            "head meta[property='og:title']",
            "content",
            "${user.displayName} | Agent Immobilier à ${locations[0].name} | ${tenants[0].name}"
        )
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:description']",
            "content",
            "Agent immobilier Ray Sponsible de Quebec,Canada. Consultez les listings de l'agent et contactez-le pour tous vos besoins immobiliers."
        )
        assertElementAttributeEndsWith(
            "head meta[property='og:url']",
            "content",
            "/agents/${agent.id}" + StringUtils.toSlug("", user.displayName)
        )

        assertCurrentPageIs(PageName.AGENT)
        assertElementPresent("#metric-container")
        assertElementPresent("#sale-listing-container")
        assertElementPresent("#rental-listing-container")
        assertElementPresent("#sold-listing-container")
        assertElementPresent("#map-container")
    }

    @Test
    fun sendMessage() {
        navigateTo("/agents/${agent.id}")

        scroll(.33)
        click("#btn-send-message")
        assertElementVisible("#koki-modal")

        input("#firstName", "Ray")
        input("#lastName", "Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        input("#message", "I am interested in your property. Please contact me.")
        input("#phone", "5147580100")
        click("#btn-send")

        val request = argumentCaptor<CreateLeadRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/leads"),
            request.capture(),
            eq(CreateLeadResponse::class.java)
        )
        assertEquals(null, request.firstValue.listingId)
        assertEquals(agent.userId, request.firstValue.agentUserId)
        assertEquals(LeadSource.AGENT, request.firstValue.source)
        assertEquals("Ray", request.firstValue.firstName)
        assertEquals("Sponsible", request.firstValue.lastName)
        assertEquals("ray.sponsible@gmail.com", request.firstValue.email)
        assertEquals("I am interested in your property. Please contact me.", request.firstValue.message)
        assertEquals("+15147580100", request.firstValue.phoneNumber)
        assertEquals("CA", request.firstValue.country)
        assertNotNull(request.firstValue.cityId)

        assertCurrentPageIs(PageName.AGENT)
        assertElementVisible("#toast-message")
    }
}
