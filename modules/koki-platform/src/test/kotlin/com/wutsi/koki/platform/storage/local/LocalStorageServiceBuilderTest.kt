package com.wutsi.koki.platform.storage.local

import kotlin.test.Test
import kotlin.test.assertEquals

class LocalStorageServiceBuilderTest {
    @Test
    fun build() {
        val builder = LocalStorageServiceBuilder("dir", "https://localhost:8080/files")

        val storage = builder.build()

        assertEquals(true, storage is LocalStorageService)
    }
}
