package com.wutsi.koki.webscraping.server.endpoint

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.file.server.endpoint.FileEndpoints
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.webscraping.dto.CreateWebpageListingResponse
import com.wutsi.koki.webscraping.dto.GetWebpageResponse
import com.wutsi.koki.webscraping.dto.SearchWebpageResponse
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.mapper.WebpageMapper
import com.wutsi.koki.webscraping.server.service.WebpageService
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/webpages")
class WebpageEndpoints(
    private val service: WebpageService,
    private val mapper: WebpageMapper,
    private val logger: KVLogger,
    private val fileEndpoints: FileEndpoints,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebpageEndpoints::class.java)
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(name = "website-id", required = false) websiteId: Long? = null,
        @RequestParam(name = "listing-id", required = false) listingId: Long? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(defaultValue = "20") limit: Int = 20,
        @RequestParam(defaultValue = "0") offset: Int = 0
    ): SearchWebpageResponse {
        val webpages = service.search(
            websiteId = websiteId,
            listingId = listingId,
            active = active,
            limit = limit,
            offset = offset,
            tenantId = tenantId
        )
        logger.add("count", webpages.size)

        return SearchWebpageResponse(
            webpages = webpages.map { mapper.toWebpageSummary(it) }
        )
    }

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long
    ): GetWebpageResponse {
        logger.add("webpage_id", id)

        val webpage = service.get(id, tenantId)
        return GetWebpageResponse(webpage = mapper.toWebpage(webpage))
    }

    @PostMapping("/{id}/listing")
    @Operation(summary = "Create the listing of the webpage")
    fun listing(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): CreateWebpageListingResponse {
        // Create
        val webpage = service.listing(id, tenantId)

        // Import images
        webpage.imageUrls.forEach { imageUrl ->
            try {
                importImage(imageUrl, webpage)
            } catch (ex: Exception) {
                LOGGER.warn("webpage#${webpage.id} - Unable to import image: $imageUrl", ex)
            }
        }

        return CreateWebpageListingResponse(
            listingId = webpage.listingId!!,
            webpageId = id
        )
    }

    private fun importImage(url: String, webpage: WebpageEntity) {
        LOGGER.info("webpage#${webpage.id} - Importing image: $url")
        fileEndpoints.create(
            request = CreateFileRequest(
                url = url,
                owner = webpage.listingId?.let { id ->
                    ObjectReference(
                        id = id,
                        type = ObjectType.LISTING
                    )
                },
            ),
            tenantId = webpage.tenantId
        )
    }
}
