package com.wutsi.koki.offer.server.endpoint

import com.wutsi.koki.offer.dto.GetOfferVersionResponse
import com.wutsi.koki.offer.dto.SearchOfferVersionResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/offer-versions")
class OfferVersionEndpoints {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetOfferVersionResponse {
        TODO()
    }

    @GetMapping("/")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "offer-id") offerId: Long? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchOfferVersionResponse {
        TODO()
    }
}
