package com.wutsi.koki.portal.pub.file.mapper

import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.common.service.Moment
import com.wutsi.koki.portal.pub.file.model.FileModel
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class FileMapper(private val moment: Moment) : TenantAwareMapper() {
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
            contentUrl = entity.url,
            contentType = entity.contentType,
            contentLength = entity.contentLength,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayLanguage },
            status = entity.status,
        )
    }
}
