package com.wutsi.koki.email.server.endpont

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.GetEmailResponse
import com.wutsi.koki.email.dto.SearchEmailResponse
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.dto.SendEmailResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/emails")
class EmailEndpoints {
    @PostMapping
    fun send(@Valid @RequestBody request: SendEmailRequest): SendEmailResponse {
        TODO()
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): GetEmailResponse {
        TODO()
    }

    @GetMapping
    fun search(
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchEmailResponse {
        TODO()
    }
}
