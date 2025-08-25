package com.wutsi.koki.file.server.service.extractor

import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import kotlin.test.Test

class ImageInfoExtractorTest {
    private val extractor = ImageInfoExtractor()

    @Test
    fun jpg() {
        // GIVEN
        val uri = ImageInfoExtractorTest::class.java.getResource("/fs/file/document.jpg")!!.toURI()
        val file = File(uri)

        // WHEN
        val info = extractor.extract(file)

        // THEN
        assertEquals(null, info.numberOfPages)
        assertEquals(null, info.language)
        assertEquals(1000, info.width)
        assertEquals(631, info.height)
    }
}
