package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.message.server.mapper.MessageMapper
import com.wutsi.koki.message.server.service.MessageService
import com.wutsi.koki.workflow.dto.MessageSortBy
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchMessageEndpoint(
    private val service: MessageService,
    private val mapper: MessageMapper,
) {
    @GetMapping("/v1/messages")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "name") names: List<String> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "sort-by") sortBy: MessageSortBy? = null,
        @RequestParam(required = false, name = "asc") ascending: Boolean = true,
    ): SearchMessageResponse {
        val messages = service.search(
            tenantId = tenantId,
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
            ascending = ascending
        )
        return SearchMessageResponse(
            messages = messages.map { message -> mapper.toMessageSummary(message) }
        )
    }
}
