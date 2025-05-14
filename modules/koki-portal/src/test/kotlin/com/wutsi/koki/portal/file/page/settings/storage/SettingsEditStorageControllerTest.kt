package com.wutsi.koki.portal.file.page.settings.storage

import com.amazonaws.AmazonClientException
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.file.service.S3Validator
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsEditStorageControllerTest : AbstractPageControllerTest() {
    @MockitoBean
    private lateinit var validator: S3Validator

    @Test
    fun native() {
        disableAllConfigs()

        navigateTo("/settings/files/storage/edit")
        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE_EDIT)
        select("#type", 1)
        assertElementNotVisible(".s3")
        click("button[type=submit]")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )

        assertEquals("KOKI", request.firstValue.values[ConfigurationName.STORAGE_TYPE])

        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun s3() {
        disableAllConfigs()

        navigateTo("/settings/files/storage/edit")
        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE_EDIT)
        select("#type", 2)
        Thread.sleep(1000L)

        input("#s3Bucket", "test")
        select("#s3Region", 2)
        input("#s3AccessKey", "ACC-0000")
        input("#s3SecretKey", "SEC-0000")
        click("button[type=submit]")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )

        assertEquals("S3", request.firstValue.values[ConfigurationName.STORAGE_TYPE])
        assertEquals("test", request.firstValue.values[ConfigurationName.STORAGE_S3_BUCKET])
        assertEquals("ap-east-1", request.firstValue.values[ConfigurationName.STORAGE_S3_REGION])
        assertEquals("SEC-0000", request.firstValue.values[ConfigurationName.STORAGE_S3_SECRET_KEY])
        assertEquals("ACC-0000", request.firstValue.values[ConfigurationName.STORAGE_S3_ACCESS_KEY])

        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `S3 validation error`() {
        doThrow(AmazonClientException("Failed")).whenever(validator).validate(any(), any(), any(), any())

        navigateTo("/settings/files/storage/edit")
        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE_EDIT)
        select("#type", 2)
        Thread.sleep(1000L)

        input("#s3Bucket", "test")
        select("#s3Region", 2)
        input("#s3AccessKey", "ACC-0000")
        input("#s3SecretKey", "SEC-0000")
        click("button[type=submit]", 100)

        assertElementPresent(".alert-danger")
        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE_EDIT)

        verify(rest, never()).postForEntity(any<String>(), any(), eq(Any::class.java))
    }

    @Test
    fun back() {
        navigateTo("/settings/files/storage/edit")
        click(".btn-back")
        assertCurrentPageIs(PageName.FILE_SETTINGS_STORAGE)
    }

    @Test
    fun `show - without permission file-admin`() {
        setUpUserWithoutPermissions(listOf("file:admin"))
        navigateTo("/settings/files/storage/edit")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/files/storage/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
