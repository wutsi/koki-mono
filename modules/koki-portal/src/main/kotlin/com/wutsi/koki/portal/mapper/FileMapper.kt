package com.wutsi.koki.portal.mapper

import com.wutsi.koki.file.dto.File
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.portal.model.FileModel
import com.wutsi.koki.portal.model.UserModel
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.text.StringCharacterIterator

@Service
class FileMapper {
    fun toFileModel(
        entity: FileSummary,
        createdBy: UserModel?,
    ): FileModel {
        val fmt = createDateFormat()

        return FileModel(
            id = entity.id,
            name = entity.name,
            contentUrl = entity.url,
            contentType = entity.contentType,
            contentLength = entity.contentLength,
            contentLengthText = toFileSizeText(entity.contentLength),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            createdBy = createdBy,
            extension = FilenameUtils.getExtension(entity.name).lowercase(),
        )
    }

    fun toFileModel(
        entity: File,
        createdBy: UserModel?,
    ): FileModel {
        val fmt = createDateFormat()

        return FileModel(
            id = entity.id,
            name = entity.name,
            contentUrl = entity.url,
            contentType = entity.contentType,
            contentLength = entity.contentLength,
            contentLengthText = toFileSizeText(entity.contentLength),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            createdBy = createdBy,
            formId = entity.formId,
            workflowInstanceId = entity.workflowInstanceId,
            extension = FilenameUtils.getExtension(entity.name).lowercase(),
        )
    }

    private fun createDateFormat(): DateFormat {
        return SimpleDateFormat("yyyy/MM/dd HH:mm")
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
