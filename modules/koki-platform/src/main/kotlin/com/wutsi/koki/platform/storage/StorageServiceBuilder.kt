package com.wutsi.koki.platform.storage

import com.wutsi.koki.platform.storage.koki.KokiStorageServiceBuilder
import com.wutsi.koki.platform.storage.local.LocalStorageServiceBuilder
import com.wutsi.koki.platform.storage.s3.S3StorageServiceBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName

class StorageServiceBuilder(
    private val koki: KokiStorageServiceBuilder,
    private val local: LocalStorageServiceBuilder,
    private val s3: S3StorageServiceBuilder,
) {
    fun default(): StorageService {
        return koki.build()
    }

    fun build(config: Map<String, String>): StorageService {
        val type = config[ConfigurationName.STORAGE_TYPE]?.let { value ->
            StorageType.valueOf(value)
        } ?: StorageType.KOKI

        return when (type) {
            StorageType.S3 -> s3.build(config)
            StorageType.LOCAL -> local.build()
            StorageType.KOKI -> koki.build()
        }
    }
}
