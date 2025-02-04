package com.wutsi.koki.platform.storage.local

import com.wutsi.koki.platform.storage.StorageService

class LocalStorageServiceBuilder(
    private val directory: String,
    private val baseUrl: String,
) {
    fun build(): StorageService {
        return LocalStorageService(directory, baseUrl)
    }
}
