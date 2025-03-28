package com.wutsi.koki.file.server.service.extractor

import com.wutsi.koki.file.server.service.FileInfo
import com.wutsi.koki.file.server.service.FileInfoExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream

@Service
class DOCXInfoExtractor(
    private val languageDetector: LanguageDetector
) : FileInfoExtractor {
    override fun extract(file: File): FileInfo {
        val input = FileInputStream(file)
        input.use {
            val doc = XWPFDocument(input)
            val text = doc.paragraphs
                .map { paragraph -> paragraph.text }
                .joinToString(separator = "\n")

            return FileInfo(
                numberOfPages = doc.properties?.extendedProperties?.underlyingProperties?.pages,
                language = languageDetector.detect(text).language
            )
        }
    }
}
