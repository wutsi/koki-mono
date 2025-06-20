package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.contact.dto.CreateContactRequest
import com.wutsi.koki.contact.dto.CreateContactResponse
import com.wutsi.koki.contact.dto.GetContactResponse
import com.wutsi.koki.contact.dto.SearchContactResponse
import com.wutsi.koki.contact.dto.UpdateContactRequest
import com.wutsi.koki.contact.server.mapper.ContactMapper
import com.wutsi.koki.contact.server.service.ContactService
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
@RequestMapping("/v1/contacts")
class ContactEndpoints(
    private val service: ContactService,
    private val mapper: ContactMapper,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateContactRequest,
    ): CreateContactResponse {
        val contact = service.create(request, tenantId)
        return CreateContactResponse(contact.id!!)
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateContactRequest,
    ) {
        service.update(id, request, tenantId)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        service.delete(id, tenantId)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetContactResponse {
        val contact = service.get(id, tenantId)
        return GetContactResponse(
            contact = mapper.toContact(contact)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "contact-type-id") contactTypeIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "account-id") accountIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "created-by-id") createdByIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "account-manager-id") accountManagerIds: List<Long> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchContactResponse {
        val contacts = service.search(
            tenantId = tenantId,
            keyword = keyword,
            ids = ids,
            contactTypeIds = contactTypeIds,
            accountIds = accountIds,
            createdByIds = createdByIds,
            accountManagerIds = accountManagerIds,
            limit = limit,
            offset = offset
        )
        return SearchContactResponse(
            contacts = contacts.map { contact -> mapper.toContactSummary(contact) }
        )
    }
}
