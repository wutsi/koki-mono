package com.wutsi.koki.portal.client.file.mapper

import com.wutsi.koki.file.dto.File
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.portal.client.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.client.common.service.Moment
import com.wutsi.koki.portal.client.file.model.FileModel
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.text.StringCharacterIterator

@Service
class FileMapper(private val moment: Moment) : TenantAwareMapper() {
    fun toFileModel(entity: FileSummary): FileModel {
        val fmt = createDateTimeFormat()
        return FileModel(
            id = entity.id,
            name = entity.name,
            contentUrl = entity.url,
            contentType = entity.contentType,
            contentLength = entity.contentLength,
            contentLengthText = toFileSizeText(entity.contentLength),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
            extension = FilenameUtils.getExtension(entity.name).lowercase(),
        )
    }

    fun toFileModel(entity: File): FileModel {
        val fmt = createDateTimeFormat()
        return FileModel(
            id = entity.id,
            name = entity.name,
            contentUrl = entity.url,
            contentType = entity.contentType,
            contentLength = entity.contentLength,
            contentLengthText = toFileSizeText(entity.contentLength),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
            extension = FilenameUtils.getExtension(entity.name).lowercase(),
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
}
