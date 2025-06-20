package com.wutsi.koki.portal.account.page

import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiresPermission(["account", "account:full_access"])
class AccountSelectorController(private val service: AccountService) : AbstractAccountController() {
    @GetMapping("/accounts/selector/search")
    fun search(
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): List<AccountModel> {
        val user = userHolder.get()
        return service.accounts(
            keyword = keyword,
            managedByIds = if (user?.hasFullAccess("account") == true) emptyList() else listOf(user?.id ?: -1),
            limit = limit,
            offset = offset,
            fullGraph = false,
        )
    }
}
