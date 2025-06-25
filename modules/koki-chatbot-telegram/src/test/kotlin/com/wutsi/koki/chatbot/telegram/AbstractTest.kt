package com.wutsi.koki.chatbot.telegram

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.refdata.dto.GetLocationResponse
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.room.dto.SearchRoomLocationMetricResponse
import com.wutsi.koki.tenant.dto.GetTenantResponse
import com.wutsi.koki.tenant.dto.SearchTenantResponse
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.client.RestTemplate

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractTest {
    @LocalServerPort
    protected val port: Int = 0

    @Value("\${koki.sdk.base-url}")
    protected lateinit var sdkBaseUrl: String

    @MockitoBean
    protected lateinit var rest: RestTemplate

    @MockitoBean
    @Qualifier("RestWithoutTenantHeader")
    protected lateinit var restWithoutTenantHeader: RestTemplate

    @MockitoBean
    @Qualifier("RestForAuthentication")
    protected lateinit var restForAuthentication: RestTemplate

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        setupDefaultApiResponses()
    }

    protected fun setupDefaultApiResponses() {
        setupRefDataModule()
        setupTenantModule()
        setupRoomModule()
    }

    private fun setupRefDataModule() {
        // Location
        doReturn(
            ResponseEntity(
                SearchLocationResponse(RefDataFixtures.locations),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchLocationResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetLocationResponse(RefDataFixtures.locations[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetLocationResponse::class.java)
            )
    }

    private fun setupTenantModule() {
        // Tenant
        doReturn(
            ResponseEntity(
                SearchTenantResponse(TenantFixtures.tenants),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchTenantResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetTenantResponse(TenantFixtures.tenants[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetTenantResponse::class.java)
            )
    }

    fun setupRoomModule() {
        // Location Metrics
        doReturn(
            ResponseEntity(
                SearchRoomLocationMetricResponse(RoomFixtures.metrics),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoomLocationMetricResponse::class.java)
            )
    }
}
