package com.wutsi.koki.room.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.LabelService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.room.server.service.ai.RoomImageAgent
import com.wutsi.koki.room.server.service.data.RoomImageAgentData
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URI

@Service
class RoomMQConsumer(
    private val fileService: FileService,
    private val configurationService: ConfigurationService,
    private val labelService: LabelService,
    private val storageServiceProvider: StorageServiceProvider,
    private val llmProvider: LLMProvider,
    private val logger: KVLogger,
    private val objectMapper: ObjectMapper,
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
        logger.add("file_id", event.fileId)
        logger.add("tenant_id", event.tenantId)
        logger.add("owner_id", event.owner?.id)
        logger.add("owner_type", event.owner?.type)

        if (event.owner?.type != ObjectType.ROOM || !isAIEnabled(event.tenantId)) {
            return
        }

        val file = fileService.get(event.fileId, event.tenantId)
        logger.add("file_type", file.type)
        if (file.type != FileType.IMAGE) {
            return
        }

        // Extract
        val llm = llmProvider.get(event.tenantId)
        val agent = RoomImageAgent(llm = llm)
        val f = download(file) ?: return
        try {
            val query = "CExtract the information from the image provided"
            val result = agent.run(query, f)

            // Update file
            val data = objectMapper.readValue(result, RoomImageAgentData::class.java)
            file.title = data.title
            file.description = data.description
            file.status = if (data.valid) FileStatus.APPROVED else FileStatus.REJECTED
            file.rejectionReason = if (!data.valid) data.reason else null
            if (data.hashtags?.isNotEmpty() == true) {
                file.labels = labelService.findOrCreate(tenantId = event.tenantId,
                    names = data.hashtags.map { tag -> if (tag.startsWith("#")) tag.substring(1) else tag })
            }
            fileService.save(file)
        } finally {
            f.delete()
        }
    }

    private fun download(file: FileEntity): File? {
        if (!isContentTypeSupported(file.contentType)) {
            return null
        }
        val extension = FilenameUtils.getExtension(file.url)
        val f = File.createTempFile(file.name, ".$extension")
        val output = FileOutputStream(f)
        output.use {
            storageServiceProvider.get(file.tenantId).get(URI(file.url).toURL(), output)
            return f
        }
    }

    private fun isContentTypeSupported(contentType: String): Boolean {
        return contentType.startsWith("text/") || contentType.startsWith("image/") || contentType == "application/pdf"
    }

    private fun isAIEnabled(tenantId: Long): Boolean {
        val configs = configurationService.search(
            tenantId = tenantId, names = listOf(
                ConfigurationName.AI_PROVIDER,
            )
        )
        return configs.size >= 1
    }
}
