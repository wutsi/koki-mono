package com.wutsi.koki.service.server.service.auth

import com.wutsi.koki.service.server.domain.ServiceEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ApiKeyAuthorizationHeaderProviderTest {
    val provider = BasicAuthorizationHeaderProvider()

    @Test
    fun `no credentials`() {
        val service = ServiceEntity(apiKey = null)
        assertNull(provider.get(service))
    }

    @Test
    fun apiKey() {
        val service = ServiceEntity(apiKey = "1111")
        assertEquals("Bearer 1111", provider.get(service))
    }
}
