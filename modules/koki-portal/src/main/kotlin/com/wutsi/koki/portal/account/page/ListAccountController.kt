package com.wutsi.koki.portal.account.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
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
    private val typeService: TypeService,
) : AbstractAccountController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListAccountController::class.java)
    }

    @GetMapping("/accounts")
    fun list(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "type-id") typeId: Long? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
        model: Model
    ): String {
        more(
            typeId = typeId,
            limit = limit,
            offset = offset,
            model = model
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ACCOUNT_LIST,
                title = "Accounts",
            )
        )

        loadToast(referer, toast, timestamp, operation, model)

        model.addAttribute("typeId", typeId)

        return "accounts/list"
    }

    @GetMapping("/accounts/more")
    fun more(
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false, name = "type-id") typeId: Long? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute(
            "types",
            typeService.types(objectType = ObjectType.ACCOUNT, active = true, limit = Integer.MAX_VALUE)
        )

        val accounts = service.accounts(
            accountTypeIds = typeId?.let { listOf(typeId) } ?: emptyList(),
            limit = limit,
            offset = offset,
        )
        if (accounts.isNotEmpty()) {
            model.addAttribute("accounts", accounts)

            if (accounts.size >= limit) {
                val nextOffset = offset + limit
                var url = "/accounts/more.html?limit=$limit&offset=$nextOffset"
                if (collection != null) {
                    url = "$url&col=$collection"
                }
                if (typeId != null) {
                    url = "$url&type-id=$typeId"
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
}
