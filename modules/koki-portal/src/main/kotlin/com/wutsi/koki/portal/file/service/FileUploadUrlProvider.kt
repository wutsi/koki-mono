package com.wutsi.koki.portal.file.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.sdk.KokiFiles
import org.springframework.stereotype.Service

@Service
class FileUploadUrlProvider(
    private val koki: KokiFiles
) {
    fun get(
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        type: FileType,
    ): String {
        return koki.uploadUrl(
            ownerId = ownerId,
            ownerType = ownerType,
            type = type,
        )
    }
}
