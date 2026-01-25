package com.wutsi.koki.file.server.service.mq

import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileInfoExtractor
import com.wutsi.koki.file.server.service.FileInfoExtractorProvider
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tenant.server.service.StorageProvider
import org.slf4j.LoggerFactory
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
    companion object {
        private val LOGGER = LoggerFactory.getLogger(FileUploadedEventHandler::class.java)
    }

    fun handle(event: FileUploadedEvent): Boolean {
        logger.add("event_file_id", event.fileId)
        logger.add("event_file_type", event.fileType)
        logger.add("event_tenant_id", event.tenantId)
        logger.add("event_owner_id", event.owner?.id)
        logger.add("event_owner_type", event.owner?.type)

        val file = fileService.get(id = event.fileId, tenantId = event.tenantId)
        val extractor = extractorProvider.get(file.contentType)
        if (extractor != null) {
            try {
                when (file.type) {
                    FileType.FILE -> return updateFileInfo(file, extractor)
                    FileType.IMAGE -> return updateImageInfo(file, extractor)
                    else -> {}
                }
            } catch (ex: Exception) {
                logger.add("file_extraction_exception", ex::class.java.name)
                logger.add("file_extraction_error", ex.message)

                LOGGER.warn("Unable to extract information for file: ${file.id}", ex)
            }
        }
        return false
    }

    private fun updateFileInfo(file: FileEntity, extractor: FileInfoExtractor): Boolean {
        if (file.numberOfPages == null || file.language == null) {
            val f = download(file)
            val infos = extractor.extract(f)
            file.numberOfPages = infos.numberOfPages
            file.language = infos.language
            fileService.save(file)
            return true
        }
        return false
    }

    private fun updateImageInfo(file: FileEntity, extractor: FileInfoExtractor): Boolean {
        if (file.width == null || file.height == null) {
            val f = download(file)
            val infos = extractor.extract(f)
            file.width = infos.width
            file.height = infos.height
            fileService.save(file)
            return true
        }
        return false
    }

    private fun download(file: FileEntity): File {
        val f = File.createTempFile("file-${file.id}", "tmp")
        f.deleteOnExit()
        val output = FileOutputStream(f)
        output.use {
            storageProvider.get(file.tenantId).get(URI(file.url).toURL(), output)
        }
        return f
    }
}
