package com.wutsi.koki.file.server.service.extractor

import org.apache.tika.language.detect.LanguageDetector
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import kotlin.test.Test

class DOCXInfoExtractorTest {
    private val languageDetector = LanguageDetector.getDefaultLanguageDetector().loadModels()
    private val extractor = DOCXInfoExtractor(languageDetector)

    @Test
    fun fr() {
        // GIVEN
        val uri = PDFInfoExtractorTest::class.java.getResource("/fs/file/document-fr.docx")!!.toURI()
        val file = File(uri)

        // WHEN
        val info = extractor.extract(file)

        // THEN
        assertEquals(467, info.numberOfPages)
        assertEquals("fr", info.language)
        assertEquals(null, info.width)
        assertEquals(null, info.height)
    }
}
