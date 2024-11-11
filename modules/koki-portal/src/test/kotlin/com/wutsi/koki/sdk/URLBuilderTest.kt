package com.wutsi.koki.portal.rest

import kotlin.test.Test
import kotlin.test.assertEquals

class URLBuilderTest {
    private val builder = URLBuilder("http://localhost:8080")

    @Test
    fun build() {
        val url = builder.build("/run", mapOf("id" to 111))
        assertEquals("http://localhost:8080/run?id=111", url)
    }

    @Test
    fun `parameter with list`() {
        val url = builder.build("/run", mapOf("id" to 111, "choice" to listOf("1", "3")))
        assertEquals("http://localhost:8080/run?id=111&choice=1&choice=3", url)
    }

    @Test
    fun `ignore parameters with null value`() {
        val url = builder.build("/run", mapOf("id" to 111, "choice" to null))
        assertEquals("http://localhost:8080/run?id=111", url)
    }

    @Test
    fun `no parameters`() {
        val url = builder.build("/run")
        assertEquals("http://localhost:8080/run", url)
    }
}
