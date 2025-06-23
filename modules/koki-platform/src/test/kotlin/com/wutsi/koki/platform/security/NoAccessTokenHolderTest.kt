package com.wutsi.koki.platform.security

import kotlin.test.Test
import kotlin.test.assertEquals

class NoAccessTokenHolderTest {
    private val holder = NoAccessTokenHolder()

    @Test
    fun get() {
        assertEquals(null, holder.get())
    }

    @Test
    fun set() {
        holder.set("54059409")
        assertEquals(null, holder.get())
    }

    @Test
    fun remove() {
        holder.remove()
    }
}
