package com.wutsi.koki.listing.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AiqsBatchService(
    private val listingService: ListingService,
    private val fileService: FileService,
    private val averageImageQualityScoreService: AverageImageQualityScoreService,
    private val logger: KVLogger,
) {
    companion object {
        private const val BATCH_SIZE = 100
        private val VALID_STATUSES = listOf(
            ListingStatus.ACTIVE,
            ListingStatus.ACTIVE_WITH_CONTINGENCIES,
            ListingStatus.SOLD,
            ListingStatus.RENTED,
            ListingStatus.PENDING,
        )
    }

    @Async
    fun computeAll(tenantId: Long) {
        var offset = 0
        var totalProcessed = 0
        var totalUpdated = 0

        try {
            do {
                val listings = listingService.search(
                    tenantId = tenantId,
                    statuses = VALID_STATUSES,
                    limit = BATCH_SIZE,
                    offset = offset,
                )

                listings.forEach { listing ->
                    val images = fileService.search(
                        tenantId = tenantId,
                        ownerId = listing.id,
                        ownerType = ObjectType.LISTING,
                        status = FileStatus.APPROVED,
                        type = FileType.IMAGE,
                        limit = 100,
                    )

                    val aiqs = averageImageQualityScoreService.compute(images)
                    if (listing.averageImageQualityScore != aiqs) {
                        listing.averageImageQualityScore = aiqs
                        listingService.save(listing)
                        totalUpdated++
                    }
                    totalProcessed++
                }

                offset += BATCH_SIZE
            } while (listings.size == BATCH_SIZE)

            logger.add("aiqs_batch_total_processed", totalProcessed)
            logger.add("aiqs_batch_total_updated", totalUpdated)
            logger.add("aiqs_batch_success", true)
        } catch (ex: Exception) {
            logger.add("aiqs_batch_success", false)
            logger.add("aiqs_batch_error", ex.message)
            throw ex
        }
    }
}
