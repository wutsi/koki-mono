package com.wutsi.koki.portal.security

import kotlin.test.Test
import kotlin.test.assertEquals

class AccessTokenPrincipalTest {
    @Test
    fun name() {
        val principal = AccessTokenPrincipal("xx")
        assertEquals("xx", principal.name)
    }
}
