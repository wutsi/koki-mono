package com.wutsi.koki.file.server.service

import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@Service
class FileConsumer(
    private val storageBuilder: StorageServiceBuilder,
    private val configurationService: ConfigurationService,
    private val extractorProvider: FileInfoExtractorProvider,
    private val fileService: FileService,
    private val logger: KVLogger,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is FileUploadedEvent) {
            onFileUploaded(event)
        } else {
            return false
        }
        return true
    }

    private fun onFileUploaded(event: FileUploadedEvent) {
        logger.add("event_file_id", event.fileId)
        logger.add("event_tenant_id", event.tenantId)

        val file = fileService.get(id = event.fileId, tenantId = event.tenantId)
        val f = download(file)
        try {
            val infos = extractorProvider.get(file.contentType)?.extract(f)
            if (infos != null) {
                file.numberOfPages = infos.numberOfPages
                file.language = infos.language
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
            getStorageService(file.tenantId).get(URL(file.url), output)
        }
        return f
    }

    private fun getStorageService(tenantId: Long): StorageService {
        val configs = configurationService.search(
            tenantId = tenantId, keyword = "storage."
        ).map { config -> config.name to config.value }.toMap()
        return storageBuilder.build(configs)
    }
}
