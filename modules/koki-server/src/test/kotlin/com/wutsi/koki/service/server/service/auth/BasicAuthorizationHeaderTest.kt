package com.wutsi.koki.service.server.service.auth

import com.wutsi.koki.service.server.domain.ServiceEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BasicAuthorizationHeaderTest {
    val valueProvider = BasicAuthorizationHeader()

    @Test
    fun `no credentials`() {
        val service = ServiceEntity(username = "", password = null)
        assertNull(valueProvider.value(service))
    }

    @Test
    fun `no password`() {
        val service = ServiceEntity(username = "ray.sponsible", password = null)
        assertEquals("Basic cmF5LnNwb25zaWJsZTo=", valueProvider.value(service))
    }

    @Test
    fun `username and password`() {
        val service = ServiceEntity(username = "ray.sponsible", password = "secret")
        assertEquals("Basic cmF5LnNwb25zaWJsZTpzZWNyZXQ=", valueProvider.value(service))
    }
}
