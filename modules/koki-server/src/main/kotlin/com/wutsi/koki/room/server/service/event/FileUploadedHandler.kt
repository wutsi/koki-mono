package com.wutsi.koki.room.server.service.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.LabelService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.room.server.service.RoomService
import com.wutsi.koki.room.server.service.ai.RoomAgentFactory
import com.wutsi.koki.room.server.service.data.RoomImageAgentData
import org.springframework.stereotype.Service

@Service
class FileUploadedHandler(
    private val fileService: FileService,
    private val labelService: LabelService,
    private val agentFactory: RoomAgentFactory,
    private val objectMapper: ObjectMapper,
    private val roomService: RoomService,

    storageServiceProvider: StorageServiceProvider,
) : AbstractRoomEventHandler(storageServiceProvider) {
    companion object {
        const val IMAGE_AGENT_QUERY = "Extract the information from the image provided"
    }

    fun handle(event: FileUploadedEvent) {
        val image = fileService.get(event.fileId, event.tenantId)
        if (image.type != FileType.IMAGE || image.ownerType != ObjectType.ROOM) {
            return
        }

        // Update Image
        val data = extractRoomInformationFromImage(image)
        if (data != null) {
            updateImage(image, data)
        }

        // Set room hero image
        event.owner?.id?.let { id -> setHeroImage(id, image) }
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

    private fun setHeroImage(roomId: Long, image: FileEntity) {
        val room = roomService.get(roomId, image.tenantId)
        if (room.heroImageId == null) {
            room.heroImageId = image.id
            roomService.save(room)
        }
    }
}
