package com.wutsi.koki.portal.file.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.file.dto.SearchFileResponse
import kotlin.test.Test

class ListFileWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/files/widgets/list?workflow-instance-id=111")

        assertElementPresent(".files-widget")
        assertElementCount(".files-widget table tr", files.size + 1)
        assertElementNotPresent(".empty-message")
    }

    @Test
    fun empty() {
        doReturn(SearchFileResponse()).whenever(kokiFiles)
            .files(
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
