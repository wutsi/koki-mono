package com.wutsi.koki.room.web.file.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.room.web.file.mapper.FileMapper
import com.wutsi.koki.room.web.file.model.FileModel
import com.wutsi.koki.sdk.KokiFiles
import org.springframework.stereotype.Service

@Service
class FileService(
    private val koki: KokiFiles,
    private val mapper: FileMapper,
) {
    fun files(
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        type: FileType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<FileModel> {
        val files = koki.files(
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            type = type,
            status = FileStatus.APPROVED,
            limit = limit,
            offset = offset
        ).files
        if (files.isEmpty()) {
            return emptyList()
        }

        return files.map { file ->
            mapper.toFileModel(
                entity = file,
            )
        }
    }
}
