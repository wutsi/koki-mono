package com.wutsi.koki.listing.server.mq

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageProvider
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.room.server.server.agent.ImageReviewerAgent
import com.wutsi.koki.room.server.server.agent.ImageReviewerAgentResult
import com.wutsi.koki.room.server.server.agent.ListingAgentFactory
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URI

@Service
class ListingFileUploadedEventHandler(
    private val agentFactory: ListingAgentFactory,
    private val fileService: FileService,
    private val listingService: ListingService,
    private val storageProvider: StorageProvider,
    private val objectMapper: ObjectMapper,
) {
    fun handle(event: FileUploadedEvent) {
        if (!accept(event)) {
            return
        }

        val file = fileService.get(event.fileId, event.tenantId)
        if (file.type == FileType.IMAGE) {
            reviewImage(file)
        } else if (file.type == FileType.FILE) {
            reviewFile(file)
        }

        updateListing(event.owner!!.id, file)
    }

    private fun accept(event: FileUploadedEvent): Boolean {
        return event.owner?.type == ObjectType.LISTING
    }

    private fun reviewImage(image: FileEntity): FileEntity {
        if (image.status != FileStatus.UNDER_REVIEW) {
            return image
        }

        val agent = agentFactory.createImageReviewerAgent(image.tenantId)
        if (agent == null) {
            image.status = FileStatus.APPROVED
        } else {
            val f = download(image)
            val json = agent.run(ImageReviewerAgent.QUERY, listOf(f))
            val result = objectMapper.readValue(json, ImageReviewerAgentResult::class.java)
            image.status = if (result.valid) FileStatus.APPROVED else FileStatus.REJECTED
            image.rejectionReason = if (result.valid) null else result.reason
            image.title = result.title
            image.titleFr = result.titleFr
            image.description = result.description
            image.descriptionFr = result.descriptionFr
            image.imageQuality = result.quality
        }
        return fileService.save(image)
    }

    private fun updateListing(listingId: Long, file: FileEntity) {
        if (file.status != FileStatus.APPROVED) {
            return
        }

        val listing = listingService.get(listingId, file.tenantId)
        if (file.type == FileType.IMAGE) {
            if (listing.heroImageId == null) {
                listing.heroImageId = file.id
            }
            listing.totalImages = fileService.countByTypeAndOwnerIdAndOwnerType(
                file.type,
                listingId,
                ObjectType.LISTING
            )
        } else if (file.type == FileType.FILE) {
            listing.totalFiles = fileService.countByTypeAndOwnerIdAndOwnerType(
                file.type,
                listingId,
                ObjectType.LISTING
            )
        }
        listingService.save(listing, file.createdById)
    }

    private fun reviewFile(file: FileEntity): FileEntity {
        file.status = FileStatus.APPROVED
        return fileService.save(file)
    }

    private fun download(file: FileEntity): File {
        val extension = FilenameUtils.getExtension(file.name)
        val f = File.createTempFile("file-${file.id}", ".$extension")
        val output = FileOutputStream(f)
        output.use {
            storageProvider.get(file.tenantId).get(URI(file.url).toURL(), output)
        }
        return f
    }
}
