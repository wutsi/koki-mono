package com.wutsi.koki.file.server.mapper

import com.wutsi.koki.file.dto.File
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.dto.LabelSummary
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.domain.LabelEntity
import org.springframework.stereotype.Service

@Service
class FileMapper {
    fun toFile(entity: FileEntity): File {
        return File(
            id = entity.id!!,
            type = entity.type,
            name = entity.name,
            title = entity.title,
            url = entity.url,
            contentLength = entity.contentLength,
            contentType = entity.contentType,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            description = entity.description,
            language = entity.language,
            numberOfPages = entity.numberOfPages,
            labels = entity.labels.map { label -> toLabelSummary(label) },
            status = entity.status,
            rejectionReason = entity.rejectionReason,
        )
    }

    fun toFileSummary(entity: FileEntity): FileSummary {
        return FileSummary(
            id = entity.id!!,
            type = entity.type,
            name = entity.name,
            title = entity.title,
            url = entity.url,
            contentLength = entity.contentLength,
            contentType = entity.contentType,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            language = entity.language,
            numberOfPages = entity.numberOfPages,
            status = entity.status,
            rejectionReason = entity.rejectionReason,
        )
    }

    private fun toLabelSummary(entity: LabelEntity): LabelSummary {
        return LabelSummary(
            id = entity.id!!,
            displayName = entity.displayName,
            createdAt = entity.createdAt,
        )
    }
}
