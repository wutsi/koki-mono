package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.server.service.PasswordEncryptor
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PasswordServiceTest {
    companion object {
        const val SALT = "...143.,.."
        const val CLEAR = "secret"
    }

    val service = PasswordEncryptor()

    @Test
    fun hash() {
        val hashed = service.hash(CLEAR, SALT)
        assertEquals("607e0b9e5496964b1385b7c10e3e2403", hashed)
        assertEquals(32, hashed.length)
    }

    @Test
    fun match() {
        val hashed = "607e0b9e5496964b1385b7c10e3e2403"
        assertTrue(service.matches(CLEAR, hashed, SALT))
    }

    @Test
    fun mismatch() {
        val hashed = "xxx"
        assertFalse(service.matches(CLEAR, hashed, SALT))
    }
}
