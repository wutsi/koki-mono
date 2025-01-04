package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.dto.CreateAccountResponse
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.account.dto.SearchAccountResponse
import com.wutsi.koki.account.dto.UpdateAccountRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/accounts")
class AccountEndpoints {
    @PostMapping
    fun create(@Valid @RequestBody request: CreateAccountRequest): CreateAccountResponse {
        TODO()
    }

    @PostMapping("/{id}")
    fun update(@PathVariable id: String, @Valid @RequestBody request: UpdateAccountRequest) {
        TODO()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        TODO()
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): GetAccountResponse {
        TODO()
    }

    @GetMapping
    fun search(): SearchAccountResponse {
        TODO()
    }
}
