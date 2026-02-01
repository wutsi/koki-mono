package com.wutsi.koki.webscraping.server.service.mq

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.webscraping.server.command.CreateWebpageListingCommand
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.service.WebpageService
import org.springframework.stereotype.Service

@Service
class CreateWebpageListingCommandHandler(
    private val service: WebpageService,
    private val logger: KVLogger,
    private val imageImporter: CreateWebpageImagesCommandHandler,
) {
    fun handle(command: CreateWebpageListingCommand): WebpageEntity {
        logger.add("command_webpage_id", command.webpageId)
        logger.add("command_tenant_id", command.tenantId)
        logger.add("command_overwrite", command.overwrite)

        // Create Listing from Webpage
        val webpage = service.listing(command.webpageId, command.tenantId)
        logger.add("listing_id", webpage.listingId)

        // Import images
        imageImporter.handle(webpage)
        return webpage
    }
}
