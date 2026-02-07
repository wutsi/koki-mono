package com.wutsi.koki.file.server.service

import com.wutsi.koki.common.dto.ObjectType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import kotlin.test.Test

class ImageResizerProviderTest {
    private val factory = ImageResizerProvider()

    @Test
    fun `register and get`() {
        val resizer = object : ImageResizer {
            override fun tinyUrl(url: String): String = url
            override fun thumbnailUrl(url: String): String = url
            override fun previewUrl(url: String): String = url
            override fun openGraphUrl(url: String): String = url
        }
        factory.register(ObjectType.LISTING, resizer)

        val result = factory.get(ObjectType.LISTING)
        assertNotNull(result)
        assertEquals(resizer, result)
    }

    @Test
    fun `no resizer`() {
        val result = factory.get(ObjectType.ACCOUNT)
        assertNull(result)
    }
}
