package com.wutsi.koki.file.server.mapper

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.file.dto.File
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.ImageResizerProvider
import org.springframework.stereotype.Service

@Service
class FileMapper(
    private val imageResizerProvider: ImageResizerProvider,
) {
    fun toFile(entity: FileEntity): File {
        val resizer = if (entity.type == FileType.IMAGE) {
            entity.ownerType?.let { type -> imageResizerProvider.get(type) }
        } else {
            null
        }

        return File(
            id = entity.id!!,
            type = entity.type,
            name = entity.name,
            title = entity.title?.ifEmpty { null },
            description = entity.description?.ifEmpty { null },
            titleFr = entity.titleFr?.ifEmpty { null },
            descriptionFr = entity.descriptionFr?.ifEmpty { null },
            url = entity.url,
            contentLength = entity.contentLength,
            contentType = entity.contentType,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            language = entity.language?.ifEmpty { null },
            numberOfPages = entity.numberOfPages,
            status = entity.status,
            rejectionReason = entity.rejectionReason?.ifEmpty { null },
            owner = toObjectReference(entity),
            width = entity.width,
            height = entity.height,
            imageQuality = entity.imageQuality,
            thumbnailUrl = resizer?.thumbnailUrl(entity.url),
            previewUrl = resizer?.previewUrl(entity.url),
            tinyUrl = resizer?.tinyUrl(entity.url),
            openGraphUrl = resizer?.openGraphUrl(entity.url),
        )
    }

    fun toFileSummary(entity: FileEntity): FileSummary {
        val resizer = if (entity.type == FileType.IMAGE) {
            entity.ownerType?.let { type -> imageResizerProvider.get(type) }
        } else {
            null
        }

        return FileSummary(
            id = entity.id!!,
            type = entity.type,
            name = entity.name,
            title = entity.title,
            titleFr = entity.titleFr,
            url = entity.url,
            contentLength = entity.contentLength,
            contentType = entity.contentType,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            status = entity.status,
            tinyUrl = resizer?.tinyUrl(entity.url),
            thumbnailUrl = resizer?.thumbnailUrl(entity.url),
            previewUrl = resizer?.previewUrl(entity.url),
            openGraphUrl = resizer?.openGraphUrl(entity.url)
        )
    }

    private fun toObjectReference(entity: FileEntity): ObjectReference? {
        return if (entity.ownerId != null && entity.ownerType != null) {
            ObjectReference(
                id = entity.ownerId,
                type = entity.ownerType,
            )
        } else {
            null
        }
    }
}
