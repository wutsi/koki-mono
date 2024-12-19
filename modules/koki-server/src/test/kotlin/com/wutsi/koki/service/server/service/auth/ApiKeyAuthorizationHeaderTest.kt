package com.wutsi.koki.service.server.service.auth

import com.wutsi.koki.service.server.domain.ServiceEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ApiKeyAuthorizationHeaderTest {
    val valueProvider = ApiKeyAuthorizationHeader()

    @Test
    fun `no credentials`() {
        val service = ServiceEntity(apiKey = null)
        assertNull(valueProvider.value(service))
    }

    @Test
    fun apiKey() {
        val service = ServiceEntity(apiKey = "1111")
        assertEquals("Bearer 1111", valueProvider.value(service))
    }
}
