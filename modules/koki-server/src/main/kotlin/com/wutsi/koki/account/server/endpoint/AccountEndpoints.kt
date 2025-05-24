package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.dto.CreateAccountResponse
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.account.dto.SearchAccountResponse
import com.wutsi.koki.account.dto.UpdateAccountRequest
import com.wutsi.koki.account.server.mapper.AccountMapper
import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.contact.server.service.ContactService
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.tax.server.service.TaxService
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
@RequestMapping("/v1/accounts")
class AccountEndpoints(
    private val service: AccountService,
    private val mapper: AccountMapper,
    private val contactService: ContactService,
    private val taxService: TaxService,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateAccountRequest,
    ): CreateAccountResponse {
        val account = service.create(request, tenantId)
        return CreateAccountResponse(account.id ?: -1)
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateAccountRequest,
    ) {
        service.update(id, request, tenantId)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        if (isUsed(id, tenantId)) {
            throw ConflictException(error = Error(ErrorCode.ACCOUNT_IN_USE))
        }
        service.delete(id, tenantId)
    }

    private fun isUsed(id: Long, tenantId: Long): Boolean {
        return contactService.search(tenantId = tenantId, accountIds = listOf(id), limit = 1).isNotEmpty() ||
            taxService.search(tenantId = tenantId, accountIds = listOf(id), limit = 1).isNotEmpty()
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetAccountResponse {
        val account = service.get(id, tenantId)
        return GetAccountResponse(
            account = mapper.toAccount(account)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "account-type-id") accountTypeIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "managed-by-id") managedByIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "created-by-id") createdByIds: List<Long> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchAccountResponse {
        val accounts = service.search(
            tenantId = tenantId,
            keyword = keyword,
            ids = ids,
            accountTypeIds = accountTypeIds,
            managedByIds = managedByIds,
            createdByIds = createdByIds,
            limit = limit,
            offset = offset,
        )
        return SearchAccountResponse(
            accounts = accounts.map { account -> mapper.toAccountSummary(account) }
        )
    }
}
