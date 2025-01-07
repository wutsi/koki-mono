package com.wutsi.koki.portal.file.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class ListFileWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/files/widgets/list?workflow-instance-id=111")

        assertElementCount(".widget-files tr.file", files.size)
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
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/files/widgets/list")

        assertElementNotPresent(".widget-files tr.file")
        assertElementPresent(".widget-files .empty-message")
    }

    @Test
    fun upload() {
        navigateTo("/files/widgets/list?workflow-instance-id=111")
        click(".btn-upload")

        assertCurrentPageIs(PageName.UPLOAD)
    }
}
