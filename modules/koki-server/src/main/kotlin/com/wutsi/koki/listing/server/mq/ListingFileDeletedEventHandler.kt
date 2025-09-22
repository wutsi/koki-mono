package com.wutsi.koki.listing.server.mq

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.KVLogger
import jdk.internal.agent.resources.agent
import org.apache.poi.hssf.usermodel.HeaderFooter.file
import org.apache.tika.mime.MediaType.image
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
        if (event.fileType == FileType.IMAGE) {
            if (listing.heroImageId == event.fileId) {
                listing.heroImageId = findHeroImage(listing)
            }
            listing.totalImages = fileService.countByTypeAndOwnerIdAndOwnerType(
                FileType.IMAGE,
                listing.id ?: -1,
                ObjectType.LISTING,
            )?.toInt()
        } else {
            listing.totalFiles = fileService.countByTypeAndOwnerIdAndOwnerType(
                FileType.FILE,
                listing.id ?: -1,
                ObjectType.LISTING,
            )?.toInt()
        }
        listingService.save(listing)
    }

    private fun accept(event: FileDeletedEvent): Boolean {
        return event.owner?.type == ObjectType.LISTING
    }

    private fun findHeroImage(listing: ListingEntity): Long? {
        val files = fileService.search(
            tenantId = listing.tenantId,
            status = FileStatus.APPROVED,
            type = FileType.IMAGE,
            limit = 1,
        )
        return files.firstOrNull()?.id
    }
}
