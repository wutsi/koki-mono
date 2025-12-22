package com.wutsi.koki.listing.server.service.mq

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.listing.server.service.ai.ListingAgentFactory
import com.wutsi.koki.listing.server.service.ai.ListingImageReviewerAgent
import com.wutsi.koki.listing.server.service.ai.ListingImageReviewerAgentResult
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper

@Service
class ListingFileUploadedEventHandler(
    private val agentFactory: ListingAgentFactory,
    private val fileService: FileService,
    private val listingService: ListingService,
    private val jsonMapper: JsonMapper,
    private val logger: KVLogger,
) {
    fun handle(event: FileUploadedEvent): Boolean {
        logger.add("event_file_id", event.fileId)
        logger.add("event_file_type", event.fileType)
        logger.add("event_tenant_id", event.tenantId)
        logger.add("event_owner_id", event.owner?.id)
        logger.add("event_owner_type", event.owner?.type)

        if (!accept(event)) {
            return false
        }

        val file = fileService.get(event.fileId, event.tenantId)
        if (event.fileType == FileType.IMAGE) {
            reviewImage(file)
        } else if (event.fileType == FileType.FILE) {
            reviewFile(file)
        }

        updateListing(event.owner!!.id, file)
        return true
    }

    private fun accept(event: FileUploadedEvent): Boolean {
        return event.owner?.type == ObjectType.LISTING
    }

    private fun reviewImage(image: FileEntity): FileEntity {
        if (image.status != FileStatus.UNDER_REVIEW) {
            logger.add("approved", false)
            logger.add("error", "Invalid status")
            logger.add("image_status", image.status)
            return image
        }

        val agent = agentFactory.createImageReviewerAgent()
        val f = fileService.download(image)
        val json = agent.run(ListingImageReviewerAgent.QUERY, listOf(f))
        val result = jsonMapper.readValue(json, ListingImageReviewerAgentResult::class.java)
        image.status = if (result.valid) FileStatus.APPROVED else FileStatus.REJECTED
        image.rejectionReason = if (result.valid) null else result.reason
        image.title = result.title
        image.titleFr = result.titleFr
        image.description = result.description
        image.descriptionFr = result.descriptionFr
        image.imageQuality = result.quality

        logger.add("approved", image.status == FileStatus.APPROVED)
        logger.add("ai_agent", agent::class.java.simpleName)
        logger.add("error", result.reason)

        return fileService.save(image)
    }

    private fun updateListing(listingId: Long, file: FileEntity) {
        val listing = listingService.get(listingId, file.tenantId)
        if (file.type == FileType.IMAGE) {
            if (listing.heroImageId == null && file.status == FileStatus.APPROVED) {
                listing.heroImageId = file.id
            }
            listing.totalImages = fileService.countByTypeAndOwnerIdAndOwnerType(
                file.type,
                listingId,
                ObjectType.LISTING,
            )?.toInt()
        } else if (file.type == FileType.FILE) {
            listing.totalFiles = fileService.countByTypeAndOwnerIdAndOwnerType(
                file.type,
                listingId,
                ObjectType.LISTING,
            )?.toInt()
        }
        listingService.save(listing, file.createdById)
    }

    private fun reviewFile(file: FileEntity): FileEntity {
        file.status = FileStatus.APPROVED
        return fileService.save(file)
    }
}
