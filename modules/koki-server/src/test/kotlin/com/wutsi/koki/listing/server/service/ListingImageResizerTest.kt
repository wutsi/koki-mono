package com.wutsi.koki.listing.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.server.service.ImageResizerProvider
import com.wutsi.koki.platform.core.image.Focus
import com.wutsi.koki.platform.core.image.Format
import com.wutsi.koki.platform.core.image.ImageService
import com.wutsi.koki.platform.core.image.Transformation
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class ListingImageResizerTest {
    private val factory = mock<ImageResizerProvider>()
    private val imageService = mock<ImageService>()
    private val resizer = ListingImageResizer(
        factory = factory,
        imageService = imageService,
        tinyWidth = 100,
        tinyHeight = 66,
        thumbnailWidth = 300,
        thumbnailHeight = 200,
        previewWidth = 1200,
        previewHeight = 800,
    )

    @Test
    fun init() {
        resizer.init()

        verify(factory).register(ObjectType.LISTING, resizer)
    }

    @Test
    fun tinyUrl() {
        doReturn("https://ima.com/1.webp").whenever(imageService).transform(any(), any())

        val result = resizer.tinyUrl("https://ima.com/1.png")

        val transformation = argumentCaptor<Transformation>()
        verify(imageService).transform(eq("https://ima.com/1.png"), transformation.capture())

        assertEquals("https://ima.com/1.webp", result)
        assertEquals(100, transformation.firstValue.dimension?.width)
        assertEquals(66, transformation.firstValue.dimension?.height)
        assertEquals(Format.WEBP, transformation.firstValue.format)
        assertEquals(Focus.AUTO, transformation.firstValue.focus)
        assertEquals(null, transformation.firstValue.aspectRatio)
    }

    @Test
    fun thumbnailUrl() {
        doReturn("https://ima.com/1.webp").whenever(imageService).transform(any(), any())

        val result = resizer.thumbnailUrl("https://ima.com/1.png")

        val transformation = argumentCaptor<Transformation>()
        verify(imageService).transform(eq("https://ima.com/1.png"), transformation.capture())

        assertEquals("https://ima.com/1.webp", result)
        assertEquals(300, transformation.firstValue.dimension?.width)
        assertEquals(200, transformation.firstValue.dimension?.height)
        assertEquals(Format.WEBP, transformation.firstValue.format)
        assertEquals(Focus.AUTO, transformation.firstValue.focus)
        assertEquals(null, transformation.firstValue.aspectRatio)
    }

    @Test
    fun previewUrl() {
        doReturn("https://ima.com/1.webp").whenever(imageService).transform(any(), any())

        val result = resizer.previewUrl("https://ima.com/1.png")

        val transformation = argumentCaptor<Transformation>()
        verify(imageService).transform(eq("https://ima.com/1.png"), transformation.capture())

        assertEquals("https://ima.com/1.webp", result)
        assertEquals(1200, transformation.firstValue.dimension?.width)
        assertEquals(800, transformation.firstValue.dimension?.height)
        assertEquals(Format.WEBP, transformation.firstValue.format)
        assertEquals(Focus.AUTO, transformation.firstValue.focus)
        assertEquals(null, transformation.firstValue.aspectRatio)
    }
}
