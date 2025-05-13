package com.wutsi.koki

import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.File
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.dto.LabelSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object FileFixtures {
    val images = listOf(
        FileSummary(
            id = 100,
            name = "T1.png",
            title = "Living room",
            contentType = "image/png",
            contentLength = 1024L * 1024,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/800/600",
            type = ObjectType.IMAGE,
            status = FileStatus.APPROVED,
        ),
        FileSummary(
            id = 101,
            name = "T2.png",
            title = null,
            contentType = "image/png",
            contentLength = 1024L * 1024,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/600/600",
            type = ObjectType.IMAGE,
            status = FileStatus.APPROVED,
        ),
        FileSummary(
            id = 102,
            name = "T3.png",
            title = null,
            contentType = "image/png",
            contentLength = 1024L * 1024,
            createdAt = DateUtils.addDays(Date(), -2),
            url = "https://picsum.photos/600/600",
            type = ObjectType.IMAGE,
            status = FileStatus.REJECTED,
            rejectionReason = "Unable to process the file"
        ),
        FileSummary(
            id = 104,
            name = "T4.png",
            title = null,
            contentType = "image/png",
            contentLength = 1024L * 1024,
            createdAt = DateUtils.addDays(Date(), -2),
            url = "https://picsum.photos/400/400",
            type = ObjectType.IMAGE,
            status = FileStatus.UNDER_REVIEW,
        ),
    )

    val image = File(
        id = 100,
        name = "T1.png",
        title = "Living room",
        description = "Cosy living room",
        contentType = "application/pdf",
        contentLength = 800L * 600 * 64L,
        createdAt = DateUtils.addDays(Date(), -5),
        createdById = users[0].id,
        url = "https://picsum.photos/800/600",
        type = ObjectType.IMAGE,
        status = FileStatus.APPROVED,
        labels = listOf(
            LabelSummary(displayName = "spa"),
            LabelSummary(displayName = "relaxation"),
            LabelSummary(displayName = "massage"),
        )
    )

    val files = listOf(
        FileSummary(
            id = 100,
            name = "T1.png",
            title = "Living room",
            contentType = "image/png",
            contentLength = 1024L * 1024,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/800/600",
            language = "en",
            numberOfPages = 540,
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
            language = "ru",
        ),
    )

    val file = File(
        id = 100,
        name = "T1.pdf",
        title = "Tax Report",
        description = "Cosy living room",
        contentType = "application/pdf",
        contentLength = 800L * 600 * 64L,
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
