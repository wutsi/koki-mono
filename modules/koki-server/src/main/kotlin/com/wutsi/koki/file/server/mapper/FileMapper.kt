package com.wutsi.koki.file.server.mapper

import com.wutsi.koki.file.dto.File
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.server.domain.FileEntity
import org.springframework.stereotype.Service

@Service
class FileMapper {
    fun toFile(entity: FileEntity): File {
        return File(
            id = entity.id!!,
            name = entity.name,
            url = entity.url,
            contentLength = entity.contentLength,
            contentType = entity.contentType,
            modifiedAt = entity.modifiedAt,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            workflowInstanceId = entity.workflowInstanceId,
            formId = entity.formId,
        )
    }

    fun toFileSummary(entity: FileEntity): FileSummary {
        return FileSummary(
            id = entity.id!!,
            name = entity.name,
            contentLength = entity.contentLength,
            contentType = entity.contentType,
            modifiedAt = entity.modifiedAt,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
        )
    }
}
