package com.wutsi.koki.portal.account.page

import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(permissions = ["account"])
class ListAccountController(
    private val service: AccountService,
    private val currentUser: CurrentUserHolder,
) : AbstractAccountController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListAccountController::class.java)

        const val COL_ALL = "1"
        const val COL_MANAGED = "2"
        const val COL_CREATED = "3"
    }

    @GetMapping("/accounts")
    fun list(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
        model: Model
    ): String {
        more(
            collection = collection,
            limit = limit,
            offset = offset,
            model = model
        )

        model.addAttribute("collection", toCollection(collection))
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ACCOUNT_LIST,
                title = "Accounts",
            )
        )

        loadToast(referer, toast, timestamp, operation, model)
        return "accounts/list"
    }

    @GetMapping("/accounts/more")
    fun more(
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val col = toCollection(collection)
        val userId = currentUser.id()
        val accounts = service.accounts(
            managedByIds = if (col == COL_MANAGED) {
                userId?.let { id -> listOf(id) } ?: emptyList()
            } else {
                emptyList()
            },
            createdByIds = if (col == COL_CREATED) {
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

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        operation: String?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/accounts/$toast", "/accounts/create"))) {
            if (operation == "del") {
                model.addAttribute("toast", "Deleted")
            } else {
                try {
                    val account = service.account(toast, fullGraph = false)
                    model.addAttribute(
                        "toast",
                        "<a href='/accounts/${account.id}'>${account.name}</a> has been saved!"
                    )
                } catch (ex: Exception) { // I
                    LOGGER.warn("Unable to load toast information for Account#$toast", ex)
                }
            }
        }
    }

    private fun toCollection(collection: String?): String {
        return when (collection) {
            COL_ALL -> COL_ALL
            COL_MANAGED -> COL_MANAGED
            COL_CREATED -> COL_CREATED
            else -> COL_ALL
        }
    }
}
