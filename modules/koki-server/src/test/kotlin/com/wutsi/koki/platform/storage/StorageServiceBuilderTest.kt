package com.wutsi.koki.platform.storage

import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.platform.storage.local.LocalStorageServiceBuilder
import com.wutsi.koki.platform.storage.s3.S3StorageService
import com.wutsi.koki.platform.storage.s3.S3StorageServiceBuilder
import org.mockito.Mockito.mock

class StoryStorageBuilderTest {
    private val local = mock<LocalStorageService>()
    private val localBuilder = mock<LocalStorageServiceBuilder>()

    private val s3 = mock<S3StorageService>()
    private val s3Builder = mock<S3StorageServiceBuilder>()
}
