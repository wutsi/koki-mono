package com.wutsi.koki.portal.file.mapper

import com.wutsi.koki.file.dto.File
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.platform.util.Moment
import com.wutsi.koki.platform.util.NumberUtils
import com.wutsi.koki.portal.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.user.model.UserModel
import org.apache.commons.io.FilenameUtils
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class FileMapper(private val moment: Moment) : TenantAwareMapper() {
    fun toFileModel(
        entity: FileSummary,
        createdBy: UserModel?,
    ): FileModel {
        val language = LocaleContextHolder.getLocale().language
        val fmt = createDateTimeFormat()
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
            contentLengthText = toFileSizeText(entity.contentLength),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
            createdBy = createdBy,
            extension = FilenameUtils.getExtension(entity.name).lowercase(),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            numberOfPages = entity.numberOfPages,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayLanguage },
            status = entity.status,
            rejectionReason = entity.rejectionReason,
        )
    }

    fun toFileModel(
        entity: File,
        createdBy: UserModel?,
    ): FileModel {
        val language = LocaleContextHolder.getLocale().language
        val fmt = createDateTimeFormat()
        return FileModel(
            id = entity.id,
            type = entity.type,
            name = entity.name,
            title = when (language) {
                "fr" -> entity.titleFr ?: entity.title
                else -> entity.title
            },
            description = when (language) {
                "fr" -> entity.descriptionFr ?: entity.description?.ifEmpty { null }
                else -> entity.description?.ifEmpty { null }
            },
            contentUrl = entity.url,
            contentType = entity.contentType,
            contentLength = entity.contentLength,
            contentLengthText = toFileSizeText(entity.contentLength),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
            createdBy = createdBy,
            extension = FilenameUtils.getExtension(entity.name).lowercase(),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            numberOfPages = entity.numberOfPages,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayLanguage },
            status = entity.status,
            rejectionReason = entity.rejectionReason,
            owner = entity.owner?.let { owner -> ObjectReferenceModel(id = owner.id, type = owner.type) }
        )
    }

    private fun toFileSizeText(value: Long): String {
        return NumberUtils.shortText(value, "#.#", "b")
    }
}
