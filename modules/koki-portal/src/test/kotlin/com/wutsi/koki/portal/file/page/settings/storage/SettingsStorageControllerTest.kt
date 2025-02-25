package com.wutsi.koki.portal.file.page.settings.storage

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.TenantFixtures
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class SettingsStorageControllerTest : AbstractPageControllerTest() {
    @Test
    fun `s3 config`() {
        setupConfiguration("S3")

        navigateTo("/settings/files/storage")
        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE)
        assertElementCount(".s3", 4)
    }

    @Test
    fun `local config`() {
        setupConfiguration("LOCAL")

        navigateTo("/settings/files/storage")
        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE)
        assertElementCount(".s3", 0)
    }

    @Test
    fun edit() {
        setupConfiguration("S3")

        navigateTo("/settings/files/storage")
        click(".btn-edit")
        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE_EDIT)
    }

    @Test
    fun back() {
        setupConfiguration("LOCAL")

        navigateTo("/settings/files/storage")
        click(".btn-back")
        assertCurrentPageIs(PageName.FILE_SETTINGS)
    }

    @Test
    fun `show - without permission file-admin`() {
        setUpUserWithoutPermissions(listOf("file:admin"))
        navigateTo("/settings/files/storage")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/files/storage")
        assertCurrentPageIs(PageName.LOGIN)
    }

    private fun setupConfiguration(storageType: String) {
        doReturn(
            ResponseEntity(
                SearchConfigurationResponse(
                    configurations = TenantFixtures.config.map { cfg ->
                        Configuration(
                            name = cfg.key,
                            value = if (cfg.key == ConfigurationName.STORAGE_TYPE) storageType else cfg.value
                        )
                    }
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchConfigurationResponse::class.java)
            )
    }
}
