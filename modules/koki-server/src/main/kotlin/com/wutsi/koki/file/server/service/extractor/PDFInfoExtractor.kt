package com.wutsi.koki.file.server.service.info

import com.wutsi.koki.file.server.service.FileInfo
import com.wutsi.koki.file.server.service.FileInfoExtractor
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.stereotype.Service
import java.io.File

@Service
class PDFInfoExtractor(
    private val languageDetector: LanguageDetector
) : FileInfoExtractor {

    override fun extract(file: File): FileInfo {
        val doc = Loader.loadPDF(file)
        val stripper = PDFTextStripper()
        stripper.startPage = 1
        stripper.endPage = doc.numberOfPages
        val txt = stripper.getText(doc)
        languageDetector.detect(txt)

        return FileInfo(
            numberOfPages = doc.numberOfPages,
            language = languageDetector.detect(txt).language
        )
    }
}
