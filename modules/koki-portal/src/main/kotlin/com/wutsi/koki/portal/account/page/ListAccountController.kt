package com.wutsi.koki.portal.account.page

import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.CurrentUserHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ListAccountController(
    private val service: AccountService,
    private val currentUser: CurrentUserHolder,
) : AbstractPageController() {
    @GetMapping("/accounts")
    fun list(
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        more(
            collection = collection,
            limit = limit,
            offset = offset,
            model = model
        )

        model.addAttribute("collection", collection)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ACCOUNT_LIST,
                title = "Accounts",
            )
        )
        return "accounts/list"
    }

    @GetMapping("/accounts/more")
    fun more(
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val userId = currentUser.id()
        val accounts = service.accounts(
            managedByIds = if (collection == null) {
                userId?.let { id -> listOf(id) } ?: emptyList()
            } else {
                emptyList()
            },
            limit = limit,
            offset = offset,
        )
        if (accounts.isNotEmpty()) {
            model.addAttribute("accounts", accounts)

            if (accounts.size >= limit) {
                val nextOffset = offset + limit
                var url = "/accounts/more?limit=$limit&offset=$nextOffset"
                if (collection != null) {
                    url = "$url&col=$collection"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "accounts/more"
    }
}
