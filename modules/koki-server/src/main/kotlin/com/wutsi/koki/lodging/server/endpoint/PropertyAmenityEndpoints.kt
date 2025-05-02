package com.wutsi.koki.lodging.server.endpoint

import com.wutsi.koki.lodging.dto.AddAmenityRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/properties")
class PropertyAmenityEndpoints {
    @PostMapping("/{id}/amenities")
    fun add(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @RequestBody @Valid request: AddAmenityRequest
    ) {
        TODO()
    }

    @DeleteMapping("/{id}/amenities/{amenityId}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @PathVariable amenityId: Long,
    ) {
        TODO()
    }
}
