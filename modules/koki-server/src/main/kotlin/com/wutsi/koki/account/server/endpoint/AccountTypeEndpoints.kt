package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.account.dto.GetAccountTypeResponse
import com.wutsi.koki.account.dto.SearchAccountTypeResponse
import com.wutsi.koki.account.server.io.AccountTypeCSVImporter
import com.wutsi.koki.account.server.mapper.AccountTypeMapper
import com.wutsi.koki.account.server.service.AccountTypeService
import com.wutsi.koki.common.dto.ImportResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/v1/account-types")
class AccountTypeEndpoints(
    private val service: AccountTypeService,
    private val mapper: AccountTypeMapper,
    private val importer: AccountTypeCSVImporter,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetAccountTypeResponse {
        val accountType = service.get(id, tenantId)
        return GetAccountTypeResponse(mapper.toAccountType(accountType))
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "name") names: List<String> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchAccountTypeResponse {
        val accountTypes = service.search(
            tenantId = tenantId,
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset
        )
        return SearchAccountTypeResponse(
            accountTypes = accountTypes.map { accountType -> mapper.toAccountTypeSummary(accountType) }
        )
    }

    @PostMapping("/csv", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun import(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestPart file: MultipartFile
    ): ImportResponse {
        return importer.import(file.inputStream, tenantId)
    }
}
