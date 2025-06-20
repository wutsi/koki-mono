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
import com.wutsi.koki.room.server.command.PublishRoomCommand
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.ai.RoomAgentFactory
import com.wutsi.koki.room.server.service.data.RoomAgentData
import com.wutsi.koki.room.server.service.event.AbstractRoomEventHandler
import org.springframework.stereotype.Service
import java.util.Date

@Service
class PublishRoomCommandHandler(
    private val fileService: FileService,
    private val roomService: RoomService,
    private val agentFactory: RoomAgentFactory,
    private val publisher: Publisher,
    private val objectMapper: ObjectMapper,

    storageServiceProvider: StorageServiceProvider,
) : AbstractRoomEventHandler(storageServiceProvider) {
    companion object {
        const val QUERY = "Extract the information from the images provided for this property"
    }

    fun handle(cmd: PublishRoomCommand) {
        val room = roomService.get(cmd.roomId, cmd.tenantId)
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

        publisher.publish(RoomPublishedEvent(cmd.roomId, cmd.tenantId))
    }

    private fun extractRoomInformationFromImages(
        room: RoomEntity,
        images: List<FileEntity>
    ): RoomAgentData? {
        val agent = agentFactory.createRoomInformationFactory(room) ?: return null

        // Download images
        val files = images.map { image -> download(image) }
        try {
            val result = agent.run(QUERY, files)
            return objectMapper.readValue(result, RoomAgentData::class.java)
        } finally {
            files.forEach { file -> file.delete() }
        }
    }

    private fun update(room: RoomEntity, images: List<FileEntity>, data: RoomAgentData?) {
        // Update the room
        if (data != null && data.valid) {
            room.title = data.title
            room.description = data.description
            room.summary = data.summary
            room.titleFr = data.titleFr
            room.descriptionFr = data.descriptionFr
            room.summaryFr = data.summaryFr
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
}
