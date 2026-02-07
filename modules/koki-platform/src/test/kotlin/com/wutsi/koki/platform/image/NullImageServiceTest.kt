package com.wutsi.koki.platform.image

import com.wutsi.koki.platform.core.image.Transformation
import kotlin.test.Test
import kotlin.test.assertEquals

class NullImageServiceTest {
    val service = NullImageService()

    @Test
    fun `transform should return the same url`() {
        val url = "https://example.com/image.jpg"
        val transformation = Transformation()
        val result = service.transform(url, transformation)
        assertEquals(url, result)
    }

    @Test
    fun `transform should return the same url when transformation is null`() {
        val url = "https://example.com/image.jpg"
        val result = service.transform(url, null)
        assertEquals(url, result)
    }
}
