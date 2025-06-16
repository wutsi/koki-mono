package com.wutsi.koki.portal.file.mapper

import com.wutsi.koki.file.dto.File
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.dto.LabelSummary
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.file.model.LabelModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.service.Moment
import com.wutsi.koki.portal.user.model.UserModel
import org.apache.commons.io.FilenameUtils
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.text.StringCharacterIterator
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
            labels = entity.labels.map { label -> toLabelModel(label) },
            status = entity.status,
            rejectionReason = entity.rejectionReason,
        )
    }

    private fun toFileSizeText(value: Long): String {
        val fmt = DecimalFormat("#.#")
        var bytes = value
        if (bytes == 0L) {
            return ""
        } else if (-1000 < bytes && bytes < 1000) {
            return bytes.toString()
        }
        val ci = StringCharacterIterator("KMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }

        return fmt.format(bytes / 1000.0) + " " + ci.current() + "b"
    }

    private fun toLabelModel(entity: LabelSummary): LabelModel {
        return LabelModel(
            id = entity.id,
            displayName = entity.displayName,
        )
    }
}
