package com.wutsi.koki.webscraping.server.endpoint

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.webscraping.dto.GetWebpageResponse
import com.wutsi.koki.webscraping.dto.SearchWebpagesResponse
import com.wutsi.koki.webscraping.server.service.WebpageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/webpages")
class WebpageEndpoints(
    private val service: WebpageService,
    private val logger: KVLogger,
) {
    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(name = "website-id", required = false) websiteId: Long? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(defaultValue = "20") limit: Int = 20,
        @RequestParam(defaultValue = "0") offset: Int = 0
    ): SearchWebpagesResponse {
        val response = service.search(
            websiteId = websiteId,
            active = active,
            limit = limit,
            offset = offset,
            tenantId = tenantId
        )
        logger.add("count", response.webpages.size)

        return response
    }

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long
    ): GetWebpageResponse {
        logger.add("webpage_id", id)

        return service.get(id, tenantId)
    }
}
