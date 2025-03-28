package com.wutsi.koki.file.server.service.info

import com.wutsi.koki.file.server.service.FileInfo
import com.wutsi.koki.file.server.service.FileInfoExtractor
import org.apache.poi.hwpf.HWPFDocument
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream

@Service
class DOCInfoExtractor(
    private val languageDetector: LanguageDetector
) : FileInfoExtractor {

    override fun extract(file: File): FileInfo {
        val input = FileInputStream(file)
        input.use {
            val doc = HWPFDocument(input)
            doc.get
            return FileInfo(
                numberOfPages = doc.pa,
                language = languageDetector.detect(doc.text).language
            )

        }
    }
}
