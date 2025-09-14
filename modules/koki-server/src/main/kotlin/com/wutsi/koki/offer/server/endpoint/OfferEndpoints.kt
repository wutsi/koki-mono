package com.wutsi.koki.offer.server.endpoint

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.CounterOfferRequest
import com.wutsi.koki.offer.dto.CreateOfferRequest
import com.wutsi.koki.offer.dto.CreateOfferResponse
import com.wutsi.koki.offer.dto.GetOfferResponse
import com.wutsi.koki.offer.dto.SearchOfferResponse
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
@RequestMapping("/v1/offers")
class OfferEndpoints {
    @PostMapping("/")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateOfferRequest,
    ): CreateOfferResponse {
        TODO()
    }

    @PostMapping("/{id}")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: CounterOfferRequest,
    ): CreateOfferResponse {
        TODO()
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetOfferResponse {
        TODO()
    }

    @GetMapping("/")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false, name = "buyer-agent-user-id") buyerAgentUserId: Long? = null,
        @RequestParam(required = false, name = "buyer-contact-id") buyerContactId: Long? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchOfferResponse {
        TODO()
    }
}
