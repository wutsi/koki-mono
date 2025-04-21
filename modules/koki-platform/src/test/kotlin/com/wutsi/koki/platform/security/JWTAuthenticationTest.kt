package com.wutsi.koki.platform.security

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.security.dto.JWTPrincipal
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class JWTAuthenticationTest {
    val principal = mock<JWTPrincipal>()

    @Test
    fun authenticated() {
        doReturn("yo").whenever(principal).name

        val auth = JWTAuthentication(principal)

        assertEquals("yo", auth.name)
        assertEquals(principal, auth.principal)
        assertEquals(null, auth.credentials)
        assertEquals(null, auth.details)
        assertEquals(0, auth.authorities?.size)
        assertEquals(true, auth.isAuthenticated)
    }

    @Test
    fun anonymous() {
        val auth = JWTAuthentication(principal)
        auth.isAuthenticated = false

        assertEquals(false, auth.isAuthenticated)
    }
}
