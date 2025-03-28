package com.wutsi.koki.file.server.service

import com.wutsi.koki.file.server.service.extractor.DOCInfoExtractor
import com.wutsi.koki.file.server.service.extractor.DOCXInfoExtractor
import com.wutsi.koki.file.server.service.extractor.PDFInfoExtractor
import com.wutsi.koki.file.server.service.extractor.TXTInfoExtractor
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class FileInfoExtractorProviderTest {
    val txt = mock<TXTInfoExtractor>()
    val pdf = mock<PDFInfoExtractor>()
    val doc = mock<DOCInfoExtractor>()
    val docx = mock<DOCXInfoExtractor>()
    val provider = FileInfoExtractorProvider(
        txt = txt,
        pdf = pdf,
        doc = doc,
        docx = docx,
    )

    @Test
    fun test() {
        assertEquals(doc, provider.get("application/msword"))
        assertEquals(docx, provider.get("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
        assertEquals(pdf, provider.get("application/pdf"))
        assertEquals(txt, provider.get("text/plain"))
        assertEquals(null, provider.get("image/png"))
    }
}
