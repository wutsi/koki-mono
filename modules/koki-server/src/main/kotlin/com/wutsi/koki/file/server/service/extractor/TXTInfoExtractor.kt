package com.wutsi.koki.file.server.service.extractor

import com.wutsi.koki.file.server.service.FileInfo
import com.wutsi.koki.file.server.service.FileInfoExtractor
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.stereotype.Service
import java.io.File

@Service
class TXTInfoExtractor(
    private val languageDetector: LanguageDetector
) : FileInfoExtractor {

    override fun extract(file: File): FileInfo {
        return FileInfo(
            numberOfPages = null,
            language = languageDetector.detect(file.readText()).language
        )
    }
}
