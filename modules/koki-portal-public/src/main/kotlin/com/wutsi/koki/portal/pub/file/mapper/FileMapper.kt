package com.wutsi.koki.portal.pub.file.mapper

import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.file.model.FileModel
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class FileMapper : TenantAwareMapper() {
    fun toFileModel(entity: FileSummary): FileModel {
        val language = LocaleContextHolder.getLocale().language
        return FileModel(
            id = entity.id,
            type = entity.type,
            name = entity.name,
            title = when (language) {
                "fr" -> entity.titleFr ?: entity.title
                else -> entity.title
            },
            url = entity.url,
            contentType = entity.contentType,
            contentLength = entity.contentLength,
            status = entity.status,
            thumbnailUrl = entity.thumbnailUrl,
            previewUrl = entity.previewUrl,
            tinyUrl = entity.tinyUrl,
        )
    }
}
