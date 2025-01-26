package com.wutsi.koki.portal.account.page

import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiresPermission(["account"])
class AccountSelectorController(private val service: AccountService) : AbstractAccountController() {
    @GetMapping("/accounts/selector/search")
    fun search(
        @RequestParam(required = false, name = "q") keyword: String? = null,
    ): List<AccountModel> {
        return service.accounts(
            keyword = keyword,
            fullGraph = false,
        )
    }
}
