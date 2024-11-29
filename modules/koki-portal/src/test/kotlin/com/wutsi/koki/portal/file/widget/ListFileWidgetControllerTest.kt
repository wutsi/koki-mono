package com.wutsi.koki.portal.file.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.UserSummary
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import java.util.Date
import kotlin.test.Test

class ListFileWidgetControllerTest : AbstractPageControllerTest() {
    private val users = listOf(
        UserSummary(id = 11L, displayName = "Ray Sponsible"),
        UserSummary(id = 12L, displayName = "Roger Milla"),
        UserSummary(id = 13L, displayName = "Omam Mbiyick"),
    )

    private val files = listOf(
        FileSummary(
            id = "f1",
            name = "T1.pdf",
            contentType = "application/pdf",
            contentLength = 1024L * 1024,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
        ),
        FileSummary(
            id = "f2",
            name = "Medical Notes.pdf",
            contentType = "application/pdf",
            contentLength = 11 * 1024L * 1024,
            createdById = users[1].id,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://pdfobject.com/pdf/sample.pdf",
        ),
        FileSummary(
            id = "f3",
            name = "Picture.png",
            contentType = "image/png",
            contentLength = 5 * 1024L * 1024,
            createdById = users[0].id,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/800/100",
        ),
        FileSummary(
            id = "f5",
            name = "Picture2.png",
            contentType = "image/png",
            contentLength = 500,
            createdById = users[0].id,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/800/100",
        ),
        FileSummary(
            id = "f6",
            name = "empty.txt",
            contentType = "text/plain",
            contentLength = 0,
            createdById = null,
            createdAt = DateUtils.addDays(Date(), -5),
            url = "https://picsum.photos/800/100.txt",
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchFileResponse(files)).whenever(kokiFile)
            .search(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        doReturn(SearchUserResponse(users)).whenever(kokiUser)
            .searchUsers(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun show() {
        navigateTo("/files/widgets/list?workflow-instance-id=111")

        assertElementPresent(".files-widget")
        assertElementCount(".files-widget table tr", files.size + 1)
        assertElementNotPresent(".empty-message")
    }

    @Test
    fun `small layout`() {
        navigateTo("/files/widgets/list?layout=small&workflow-instance-id=111")

        assertElementPresent(".files-widget")
        assertElementCount(".files-widget table tr", files.size)
        assertElementNotPresent(".empty-message")
    }

    @Test
    fun empty() {
        doReturn(SearchFileResponse()).whenever(kokiFile)
            .search(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/files/widgets/list")

        assertElementPresent(".files-widget")
        assertElementNotPresent(".files-widget table")
        assertElementPresent(".files-widget .empty-message")
    }
}
