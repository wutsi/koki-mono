package com.wutsi.koki.chatbot.telegram

import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.dto.FileType
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object FileFixtures {
    val images = listOf(
        FileSummary(
            id = 100,
            name = "T1.png",
            title = "Living room",
            contentType = "image/png",
            contentLength = 800L * 600,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/800/600",
            type = FileType.IMAGE,
            status = FileStatus.APPROVED,
        ),
        FileSummary(
            id = 101,
            name = "T2.png",
            title = null,
            contentType = "image/png",
            contentLength = 600L * 600,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/600/600",
            type = FileType.IMAGE,
            status = FileStatus.APPROVED,
        ),
        FileSummary(
            id = 102,
            name = "T3.png",
            title = null,
            contentType = "image/png",
            contentLength = 600L * 600,
            createdAt = DateUtils.addDays(Date(), -2),
            url = "https://picsum.photos/600/600",
            type = FileType.IMAGE,
            status = FileStatus.REJECTED,
            rejectionReason = "Unable to process the file"
        ),
        FileSummary(
            id = 104,
            name = "T4.png",
            title = null,
            contentType = "image/png",
            contentLength = 400L * 400L,
            createdAt = DateUtils.addDays(Date(), -2),
            url = "https://picsum.photos/400/400",
            type = FileType.IMAGE,
            status = FileStatus.UNDER_REVIEW,
        ),
        FileSummary(
            id = 105,
            name = "T5.png",
            title = null,
            contentType = "image/png",
            contentLength = 400L * 400L,
            createdAt = DateUtils.addDays(Date(), -2),
            url = "https://picsum.photos/400/400",
            type = FileType.IMAGE,
            status = FileStatus.UNDER_REVIEW,
        ),
        FileSummary(
            id = 106,
            name = "T6.png",
            title = null,
            contentType = "image/png",
            contentLength = 400L * 400L,
            createdAt = DateUtils.addDays(Date(), -2),
            url = "https://picsum.photos/400/400",
            type = FileType.IMAGE,
            status = FileStatus.UNDER_REVIEW,
        ),
        FileSummary(
            id = 107,
            name = "T7.png",
            title = null,
            contentType = "image/png",
            contentLength = 400L * 400L,
            createdAt = DateUtils.addDays(Date(), -2),
            url = "https://picsum.photos/400/400",
            type = FileType.IMAGE,
            status = FileStatus.UNDER_REVIEW,
        ),
        FileSummary(
            id = 108,
            name = "T8.png",
            title = null,
            contentType = "image/png",
            contentLength = 400L * 400L,
            createdAt = DateUtils.addDays(Date(), -2),
            url = "https://picsum.photos/400/800",
            type = FileType.IMAGE,
            status = FileStatus.UNDER_REVIEW,
        ),
    )
}
