package com.wutsi.koki.lodging.server.endpoint

import com.wutsi.koki.invoice.dto.CreateInvoiceResponse
import com.wutsi.koki.lodging.dto.CreateRoomRequest
import com.wutsi.koki.lodging.dto.GetRoomResponse
import com.wutsi.koki.lodging.dto.RootStatus
import com.wutsi.koki.lodging.dto.SearchRoomResponse
import com.wutsi.koki.lodging.dto.UpdateRoomRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/properties")
class PropertyEndpoints {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateRoomRequest,
    ): CreateInvoiceResponse {
        TODO()
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateRoomRequest,
    ) {
        TODO()
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        TODO()
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetRoomResponse {
        TODO()
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "city-id") cityId: Long? = null,
        @RequestParam(required = false, name = "status") status: RootStatus? = null,
        @RequestParam(required = false, name = "total-guests") totalGuest: Int? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchRoomResponse {
        TODO()
    }
}
