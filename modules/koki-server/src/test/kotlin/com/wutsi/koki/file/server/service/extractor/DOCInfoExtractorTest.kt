package com.wutsi.koki.file.server.service.extractor

import org.apache.tika.language.detect.LanguageDetector
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import kotlin.test.Test

class PDFInfoExtractorTest {
    private val languageDetector = LanguageDetector.getDefaultLanguageDetector().loadModels()
    private val extractor = PDFInfoExtractor(languageDetector)

    @Test
    fun en() {
        // GIVEN
        val uri = PDFInfoExtractorTest::class.java.getResource("/file/document-en.pdf")!!.toURI()
        val file = File(uri)

        // WHEN
        val info = extractor.extract(file)

        // THEN
        assertEquals(8, info.numberOfPages)
        assertEquals("en", info.language)
    }

    @Test
    fun fr() {
        // GIVEN
        val uri = PDFInfoExtractorTest::class.java.getResource("/file/document-fr.pdf")!!.toURI()
        val file = File(uri)

        // WHEN
        val info = extractor.extract(file)

        // THEN
        assertEquals(467, info.numberOfPages)
        assertEquals("fr", info.language)
    }
}
