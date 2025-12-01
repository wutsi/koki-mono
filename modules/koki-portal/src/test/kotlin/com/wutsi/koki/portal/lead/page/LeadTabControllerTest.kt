package com.wutsi.koki.portal.file.page

import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class FileTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertElementPresent(".btn-upload")
        assertElementCount(".tab-files tr.file", files.size)
        assertElementAttribute("#file-list", "data-owner-id", "111")
        assertElementAttribute("#file-list", "data-owner-type", "ACCOUNT")
    }

    @Test
    fun `read-only`() {
        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true&read-only=true")

        assertElementNotPresent(".btn-upload")
        assertElementCount(".tab-files tr.file", files.size)
        assertElementAttribute("#file-list", "data-owner-id", "111")
        assertElementAttribute("#file-list", "data-owner-type", "ACCOUNT")
    }

    @Test
    fun upload() {
        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")
        click(".btn-upload", 1000)

        assertElementVisible("#koki-modal")
    }

    @Test
    fun `list - without permission file-manage`() {
        setupUserWithoutPermissions(listOf("file:manage"))

        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertElementNotPresent(".btn-upload")
    }

    @Test
    fun `list - without permission file`() {
        setupUserWithoutPermissions(listOf("file"))

        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertCurrentPageIs(PageName.ERROR_403)
    }
}
