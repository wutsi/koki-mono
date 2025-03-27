package com.wutsi.koki.platform.storage

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.storage.koki.KokiStorageServiceBuilder
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.platform.storage.local.LocalStorageServiceBuilder
import com.wutsi.koki.platform.storage.s3.S3StorageService
import com.wutsi.koki.platform.storage.s3.S3StorageServiceBuilder
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class StorageServiceBuilderTest {
    private val local = mock<LocalStorageService>()
    private val localBuilder = mock<LocalStorageServiceBuilder>()

    private val s3 = mock<S3StorageService>()
    private val s3Builder = mock<S3StorageServiceBuilder>()

    private val koki = mock<StorageService>()
    private val kokiBuilder = mock<KokiStorageServiceBuilder>()

    private val builder = StorageServiceBuilder(
        local = localBuilder,
        s3 = s3Builder,
        koki = kokiBuilder,
    )

    @BeforeEach
    fun setUp() {
        doReturn(s3).whenever(s3Builder).build(any())
        doReturn(local).whenever(localBuilder).build()
        doReturn(koki).whenever(kokiBuilder).build()
    }

    @Test
    fun `local storage`() {
        val storage = builder.build(StorageType.LOCAL, emptyMap())
        assertEquals(local, storage)
    }

    @Test
    fun `s3 storage`() {
        val storage = builder.build(StorageType.S3, emptyMap())
        assertEquals(s3, storage)
    }

    @Test
    fun `koki storage`() {
        val storage = builder.build(StorageType.KOKI, emptyMap())
        assertEquals(koki, storage)
    }
}
