package com.wutsi.koki

import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.file.dto.File
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.dto.LabelSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object FileFixtures {
    val files = listOf(
        FileSummary(
            id = 100,
            name = "T1.pdf",
            contentType = "application/pdf",
            contentLength = 1024L * 1024,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            language = "en",
            numberOfPages = 540,
            labels = listOf(
                LabelSummary(id = 1, displayName = "2024"),
                LabelSummary(id = 1, displayName = "T4"),
            ),
        ),
        FileSummary(
            id = 101,
            name = "Medical Notes.pdf",
            contentType = "application/pdf",
            contentLength = 11 * 1024L * 1024,
            createdById = users[1].id,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://pdfobject.com/pdf/sample.pdf",
            language = "fr",
            numberOfPages = 115,
            labels = listOf(
                LabelSummary(id = 1, displayName = "2024"),
                LabelSummary(id = 1, displayName = "T5"),
            ),
        ),
        FileSummary(
            id = 103,
            name = "Picture.png",
            contentType = "image/png",
            contentLength = 5 * 1024L * 1024,
            createdById = users[0].id,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/800/100",
        ),
        FileSummary(
            id = 104,
            name = "Picture2.png",
            contentType = "image/png",
            contentLength = 500,
            createdById = users[0].id,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/800/100",
        ),
        FileSummary(
            id = 105,
            name = "empty.txt",
            contentType = "text/plain",
            contentLength = 0,
            createdById = null,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/800/100.txt",
            labels = listOf(
                LabelSummary(id = 1, displayName = "Invoice"),
            ),
            language = "ru",
        ),
    )

    val file = File(
        id = 100,
        name = "T1.pdf",
        contentType = "application/pdf",
        contentLength = 1024L * 1024,
        createdAt = DateUtils.addDays(Date(), -5),
        createdById = users[0].id,
        url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
        language = "en",
        numberOfPages = 540,
        labels = listOf(
            LabelSummary(id = 1, displayName = "2024"),
            LabelSummary(id = 1, displayName = "T4"),
        ),
    )
}
