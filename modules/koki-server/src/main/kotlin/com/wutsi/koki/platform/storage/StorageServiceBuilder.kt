package com.wutsi.koki.platform.storage

import com.wutsi.koki.platform.storage.koki.KokiStorageServiceBuilder
import com.wutsi.koki.platform.storage.local.LocalStorageServiceBuilder
import com.wutsi.koki.platform.storage.s3.S3StorageServiceBuilder

class StorageServiceBuilder(
    private val koki: KokiStorageServiceBuilder,
    private val local: LocalStorageServiceBuilder,
    private val s3: S3StorageServiceBuilder,
) {
    fun build(type: StorageType, config: Map<String, String>): StorageService {
        return when (type) {
            StorageType.KOKI -> koki.build()
            StorageType.S3 -> s3.build(config)
            StorageType.LOCAL -> local.build()
        }
    }
}
