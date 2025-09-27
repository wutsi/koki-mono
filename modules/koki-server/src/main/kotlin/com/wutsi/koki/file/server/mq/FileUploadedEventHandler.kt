package com.wutsi.koki.file.server.mq

import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileInfoExtractorProvider
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageProvider
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URI

/**
 * Extract file metadata:
 *
 * - width: Image width
 * - height: Image height
 * - numberOfPages: Number of pages (PDF, DOC, DOCX)
 * - language: Language of the content (PDF, DOC, DOCX, TXT)
 */
@Service
class FileUploadedEventHandler(
    private val extractorProvider: FileInfoExtractorProvider,
    private val fileService: FileService,
    private val storageProvider: StorageProvider,
    private val logger: KVLogger,
) {
    fun handle(event: FileUploadedEvent) {
        logger.add("event_file_id", event.fileId)
        logger.add("event_file_type", event.fileType)
        logger.add("event_tenant_id", event.tenantId)
        logger.add("event_owner_id", event.owner?.id)
        logger.add("event_owner_type", event.owner?.type)

        val file = fileService.get(id = event.fileId, tenantId = event.tenantId)
        val f = download(file)
        try {
            val infos = extractorProvider.get(file.contentType)?.extract(f)
            if (infos != null) {
                file.numberOfPages = infos.numberOfPages
                file.language = infos.language
                file.width = infos.width
                file.height = infos.height
                fileService.save(file)
            }
        } finally {
            f.delete()
        }
    }

    private fun download(file: FileEntity): File {
        val f = File.createTempFile("file-${file.id}", "tmp")
        val output = FileOutputStream(f)
        output.use {
            storageProvider.get(file.tenantId).get(URI(file.url).toURL(), output)
        }
        return f
    }
}
