package com.wutsi.koki.webscraping.server.service.mq

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.webscraping.server.command.CreateWebpageListingCommand
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.service.WebpageService
import org.springframework.stereotype.Service

@Service
class CreateWebpageListingCommandHandler(
    private val service: WebpageService,
    private val publisher: Publisher,
) {
    fun handle(command: CreateWebpageListingCommand): Boolean {
        try {
            // Create
            val webpage = service.listing(command.webpageId, command.tenantId)

            // Import images
            webpage.imageUrls.forEach { imageUrl ->
                importImage(imageUrl, webpage)
            }
            return true
        } catch (ex: ConflictException) {
            if (ex.error.code == ErrorCode.LISTING_ALREADY_CREATED) {
                return false
            } else {
                throw ex
            }
        }
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
