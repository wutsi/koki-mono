package com.wutsi.koki.webscraping.server.endpoint

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.webscraping.dto.CreateWebsiteRequest
import com.wutsi.koki.webscraping.dto.CreateWebsiteResponse
import com.wutsi.koki.webscraping.dto.GetWebsiteResponse
import com.wutsi.koki.webscraping.dto.ScrapeWebsiteRequest
import com.wutsi.koki.webscraping.dto.ScrapeWebsiteResponse
import com.wutsi.koki.webscraping.dto.SearchWebsiteResponse
import com.wutsi.koki.webscraping.dto.UpdateWebsiteRequest
import com.wutsi.koki.webscraping.server.command.CreateWebpageListingCommand
import com.wutsi.koki.webscraping.server.mapper.WebpageMapper
import com.wutsi.koki.webscraping.server.mapper.WebsiteMapper
import com.wutsi.koki.webscraping.server.service.WebpageService
import com.wutsi.koki.webscraping.server.service.WebsiteService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/websites")
class WebsiteEndpoints(
    private val service: WebsiteService,
    private val logger: KVLogger,
    private val mapper: WebsiteMapper,
    private val publisher: Publisher,
    private val webpageService: WebpageService,
    private val webpageMapper: WebpageMapper,
) {
    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(name = "id", required = false) ids: List<Long>? = null,
        @RequestParam(name = "user-id", required = false) userIds: List<Long>? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(defaultValue = "20", required = false) limit: Int = 20,
        @RequestParam(defaultValue = "0", required = false) offset: Int = 0
    ): SearchWebsiteResponse {
        val websites = service.search(
            ids = ids,
            userIds = userIds,
            active = active,
            limit = limit,
            offset = offset,
            tenantId = tenantId
        )
        logger.add("count", websites.size)
        return SearchWebsiteResponse(
            websites = websites.map { entity -> mapper.toWebsiteSummary(entity) }
        )
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetWebsiteResponse {
        val website = service.get(id, tenantId)
        return GetWebsiteResponse(website = mapper.toWebsite(website))
    }

    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateWebsiteRequest
    ): CreateWebsiteResponse {
        logger.add("request_base_url", request.baseUrl)
        logger.add("request_user_id", request.userId)
        logger.add("request_active", request.active)
        logger.add("request_base_url", request.baseUrl)
        logger.add("request_listing_url_prefix", request.listingUrlPrefix)
        logger.add("request_content_selector", request.contentSelector)
        logger.add("request_image_selector", request.imageSelector)

        val website = service.create(request, tenantId)
        logger.add("website_id", website.id)

        return CreateWebsiteResponse(websiteId = website.id ?: -1L)
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateWebsiteRequest
    ) {
        logger.add("request_active", request.active)
        logger.add("request_listing_url_prefix", request.listingUrlPrefix)
        logger.add("request_content_selector", request.contentSelector)
        logger.add("request_image_selector", request.imageSelector)

        service.update(id, request, tenantId)
    }

    @PostMapping("/{id}/scrape")
    @Operation(summary = "Scrape a website to extract its webpages")
    fun scrape(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: ScrapeWebsiteRequest,
    ): ScrapeWebsiteResponse {
        logger.add("request_test_mode", request.testMode)

        val webpages = service.scrape(id, request, tenantId)
        logger.add("webpages_imported", webpages.size)

        return ScrapeWebsiteResponse(
            webpageImported = webpages.size,
            webpages = webpages.map { webpage -> webpageMapper.toWebpage(webpage) }
        )
    }

    @PostMapping("/{id}/listings")
    @Operation(summary = "Create the listings of all the webpages of the website")
    fun listings(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        val webpages = webpageService.search(
            tenantId = tenantId,
            websiteId = id,
            listingId = null,
            active = true,
            limit = Integer.MAX_VALUE,
            offset = 0,
        )
        logger.add("webpage_count", webpages.size)

        var sumitted = 0
        webpages.forEach { webpage ->
            if (webpage.listingId == null) {
                publisher.publish(
                    CreateWebpageListingCommand(
                        tenantId = tenantId,
                        webpageId = webpage.id ?: -1,
                    )
                )
                sumitted++
            }
        }
        logger.add("webpage_submitted_count", sumitted)
    }
}
