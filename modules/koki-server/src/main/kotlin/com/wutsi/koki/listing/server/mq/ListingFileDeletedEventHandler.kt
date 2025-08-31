package com.wutsi.koki.listing.server.mq

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.stereotype.Service

@Service
class ListingFileDeletedEventHandler(
    private val fileService: FileService,
    private val listingService: ListingService,
    private val logger: KVLogger,
) {
    fun handle(event: FileDeletedEvent) {
        if (!accept(event)) {
            return
        }

        val listing = listingService.get(event.owner!!.id, event.tenantId)
        if (listing.heroImageId == event.fileId) {
            changeHeroImage(listing)
        }
    }

    private fun accept(event: FileDeletedEvent): Boolean {
        return event.owner?.type == ObjectType.LISTING
    }

    private fun changeHeroImage(listing: ListingEntity) {
        val files = fileService.search(
            tenantId = listing.tenantId,
            status = FileStatus.APPROVED,
            type = FileType.IMAGE,
            limit = 1,
        )
        listing.heroImageId = files.firstOrNull()?.id
        listingService.save(listing)

        logger.add("listing_id", listing.id)
        logger.add("listing_hero_image_id", listing.heroImageId)
    }
}
