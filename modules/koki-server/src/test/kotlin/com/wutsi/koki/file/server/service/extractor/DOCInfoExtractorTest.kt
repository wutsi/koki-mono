package com.wutsi.koki.file.server.service.extractor

import org.apache.tika.language.detect.LanguageDetector
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import kotlin.test.Test

class TXTInfoExtractorTest {
    private val languageDetector = LanguageDetector.getDefaultLanguageDetector().loadModels()
    private val extractor = DOCInfoExtractor(languageDetector)

    @Test
    fun fr() {
        // GIVEN
        val uri = PDFInfoExtractorTest::class.java.getResource("/file/document-fr.doc")!!.toURI()
        val file = File(uri)

        // WHEN
        val info = extractor.extract(file)

        // THEN
        assertEquals(null, info.numberOfPages)
        assertEquals("fr", info.language)
    }
}
