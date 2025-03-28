package com.wutsi.koki.file.server.service

import com.wutsi.koki.file.server.service.extractor.DOCInfoExtractor
import com.wutsi.koki.file.server.service.extractor.DOCXInfoExtractor
import com.wutsi.koki.file.server.service.extractor.PDFInfoExtractor
import com.wutsi.koki.file.server.service.extractor.TXTInfoExtractor
import org.springframework.stereotype.Service

@Service
class FileInfoExtractorProvider(
    private val doc: DOCInfoExtractor,
    private val docx: DOCXInfoExtractor,
    private val pdf: PDFInfoExtractor,
    private val txt: TXTInfoExtractor,
) {
    fun get(contentType: String): FileInfoExtractor? {
        return when (contentType) {
            "text/plain" -> txt
            "application/pdf" -> pdf
            "application/msword" -> doc
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> docx
            else -> null
        }
    }
}
