package com.wutsi.koki.webscraping.server.service.mq

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.webscraping.server.command.CreateWebpageImagesCommand
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.service.WebpageService
import org.springframework.stereotype.Service

@Service
class CreateWebpageImagesCommandHandler(
    private val service: WebpageService,
    private val publisher: Publisher,
    private val logger: KVLogger,
) {
    fun handle(command: CreateWebpageImagesCommand): Boolean {
        logger.add("command_webpage_id", command.webpageId)
        logger.add("command_tenant_id", command.tenantId)

        // Create Listing from Webpage
        val webpage = service.get(command.webpageId, command.tenantId)
        return handle(webpage)
    }

    fun handle(webpage: WebpageEntity): Boolean {
        if (webpage.listingId == null) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.LISTING_NOT_FOUND,
                    message = "No listing associated to webpage ${webpage.id}"
                )
            )
        }
        var imageSubmitted = 0
        webpage.imageUrls.forEach { imageUrl ->
            importImage(imageUrl, webpage)
            imageSubmitted++
        }
        logger.add("image_submitted_count", imageSubmitted)
        return true
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
