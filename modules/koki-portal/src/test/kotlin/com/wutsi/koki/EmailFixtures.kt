package com.wutsi.koki

import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.file.dto.File
import com.wutsi.koki.file.dto.FileSummary
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
        ),
        FileSummary(
            id = 101,
            name = "Medical Notes.pdf",
            contentType = "application/pdf",
            contentLength = 11 * 1024L * 1024,
            createdById = users[1].id,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://pdfobject.com/pdf/sample.pdf",
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
    )
}
