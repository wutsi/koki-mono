package com.wutsi.koki.room.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.LabelService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.room.server.service.ai.RoomAgentFactory
import com.wutsi.koki.room.server.service.data.RoomImageAgentData
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URI

@Service
class ImageUploadedHandler(
    private val fileService: FileService,
    private val labelService: LabelService,
    private val storageServiceProvider: StorageServiceProvider,
    private val agentFactory: RoomAgentFactory,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val IMAGE_AGENT_QUERY = "Extract the information from the image provided"
    }

    fun enrich(fileId: Long, tenantId: Long) {
        val image = fileService.get(fileId, tenantId)
        if (image.type != FileType.IMAGE || image.ownerType != ObjectType.ROOM) {
            return
        }

        val data = extractRoomInformationFromImage(image)
        if (data != null) {
            updateImage(image, data)
        }
    }

    private fun extractRoomInformationFromImage(image: FileEntity): RoomImageAgentData? {
        val agent = agentFactory.createRoomImageAgent(image.tenantId) ?: return null

        // Download images
        val f = download(image)
        try {
            val result = agent.run(IMAGE_AGENT_QUERY, listOf(f))
            return objectMapper.readValue(result, RoomImageAgentData::class.java)
        } finally {
            f.delete()
        }
    }

    private fun updateImage(image: FileEntity, data: RoomImageAgentData) {
        image.title = data.title
        image.description = data.description
        image.status = if (data.valid) FileStatus.APPROVED else FileStatus.REJECTED
        image.rejectionReason = if (!data.valid) data.reason else null
        if (data.hashtags?.isNotEmpty() == true) {
            image.labels = labelService.findOrCreate(tenantId = image.tenantId,
                names = data.hashtags.map { tag -> if (tag.startsWith("#")) tag.substring(1) else tag })
        }
        fileService.save(image)
    }

    private fun download(file: FileEntity): File {
        val extension = FilenameUtils.getExtension(file.url)
        val f = File.createTempFile(file.name, ".$extension")
        val output = FileOutputStream(f)
        output.use {
            storageServiceProvider.get(file.tenantId).get(URI(file.url).toURL(), output)
            return f
        }
    }
}
