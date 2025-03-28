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
            name = entity.name,
            url = entity.url,
            contentLength = entity.contentLength,
            contentType = entity.contentType,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            description = entity.description,
            language = entity.language,
            numberOfPages = entity.numberOfPages,
            labels = entity.labels.map { label -> toLabelSummary(label) }
        )
    }

    fun toFileSummary(entity: FileEntity, labels: Map<Long, List<LabelEntity>>): FileSummary {
        return FileSummary(
            id = entity.id!!,
            name = entity.name,
            url = entity.url,
            contentLength = entity.contentLength,
            contentType = entity.contentType,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            language = entity.language,
            numberOfPages = entity.numberOfPages,
            labels = labels[entity.id]?.let { items ->
                items.map { label -> toLabelSummary(label) }
            } ?: emptyList()
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
