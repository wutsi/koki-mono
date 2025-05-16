package com.wutsi.koki.room.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.room.dto.AddAmenityRequest
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.event.RoomPublishedEvent
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.ai.RoomAgentFactory
import com.wutsi.koki.room.server.service.data.RoomInformationAgentData
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.Date

@Service
class RoomPublisher(
    private val fileService: FileService,
    private val storageServiceProvider: StorageServiceProvider,
    private val roomService: RoomService,
    private val agentFactory: RoomAgentFactory,
    private val publisher: Publisher,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val INFORMATION_AGENT_QUERY = "Extract the information from the images provided for this property"
    }

    fun publish(roomId: Long, tenantId: Long) {
        val room = roomService.get(roomId, tenantId)
        if (room.status != RoomStatus.PUBLISHING) {
            return
        }

        val images = fileService.search(
            tenantId = room.tenantId,
            ownerId = room.id,
            ownerType = ObjectType.ROOM,
            type = FileType.IMAGE,
            status = FileStatus.APPROVED,
            limit = 20,
        )

        val data = extractRoomInformationFromImages(room, images)
        update(room, images, data)

        publisher.publish(RoomPublishedEvent(roomId, tenantId))
    }

    private fun extractRoomInformationFromImages(
        room: RoomEntity,
        images: List<FileEntity>
    ): RoomInformationAgentData? {
        val agent = agentFactory.createRoomInformationFactory(room) ?: return null

        // Download images
        val files = images.map { image -> download(image) }
        try {
            val result = agent.run(INFORMATION_AGENT_QUERY, files)
            return objectMapper.readValue(result, RoomInformationAgentData::class.java)
        } finally {
            files.forEach { file -> file.delete() }
        }
    }

    private fun update(room: RoomEntity, images: List<FileEntity>, data: RoomInformationAgentData?) {
        // Update the room
        if (data != null && data.valid) {
            room.title = data.title
            room.description = data.description
            room.heroImageId = images[data.heroImageIndex].id
            room.heroImageReason = data.heroImageReason
        }
        room.status = RoomStatus.PUBLISHED
        room.publishedAt = Date()
        roomService.save(room)

        // Update the amenities
        if (data != null && data.amenityIds.isNotEmpty()) {
            roomService.addAmenities(
                room.id ?: -1,
                AddAmenityRequest(amenityIds = data.amenityIds),
                room.tenantId,
            )
        }
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
