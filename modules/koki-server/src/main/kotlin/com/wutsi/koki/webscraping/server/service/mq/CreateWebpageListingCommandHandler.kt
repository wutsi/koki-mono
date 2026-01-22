package com.wutsi.koki.webscraping.server.service.mq

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.webscraping.server.command.CreateWebpageListingCommand
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.service.WebpageService
import org.springframework.stereotype.Service

@Service
class CreateWebpageListingCommandHandler(
    private val service: WebpageService,
    private val publisher: Publisher,
    private val logger: KVLogger,
) {
    fun handle(command: CreateWebpageListingCommand): WebpageEntity {
        logger.add("command_webpage_id", command.webpageId)
        logger.add("command_tenant_id", command.tenantId)

        // Create
        val webpage = service.listing(command.webpageId, command.tenantId)
        logger.add("listing_id", webpage.listingId)

        // Import images
        var imageSubmitted = 0
        webpage.imageUrls.forEach { imageUrl ->
            importImage(imageUrl, webpage)
            imageSubmitted++
        }
        logger.add("image_submitted_count", imageSubmitted)
        return webpage
    }

    private fun importImage(url: String, webpage: WebpageEntity) {
        publisher.publish(
            CreateFileCommand(
                url = url,
                tenantId = webpage.tenantId,
                owner = webpage.listingId?.let { id ->
                    ObjectReference(
                        id = id,
                        type = ObjectType.LISTING
                    )
                },
            )
        )
    }
}
