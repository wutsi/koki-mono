package com.wutsi.koki.portal.client.tax.service

import com.wutsi.koki.portal.client.account.service.AccountService
import com.wutsi.koki.portal.client.security.service.CurrentUserHolder
import com.wutsi.koki.portal.client.tax.mapper.TaxMapper
import com.wutsi.koki.portal.client.tax.model.TaxModel
import com.wutsi.koki.portal.client.tenant.service.TypeService
import com.wutsi.koki.sdk.KokiTaxes
import org.springframework.stereotype.Service

@Service
class TaxService(
    private val koki: KokiTaxes,
    private val mapper: TaxMapper,
    private val typeService: TypeService,
    private val accountService: AccountService,
    private val userHolder: CurrentUserHolder,
) {
    fun tax(id: Long): TaxModel {
        val tax = koki.tax(id).tax
        val taxType = tax.taxTypeId?.let { id -> typeService.type(id) }
        val account = accountService.account(tax.accountId)
        return mapper.toTax(
            entity = tax,
            taxType = taxType,
            account = account,
        )
    }

    fun taxes(
        limit: Int = 20,
        offset: Int = 0,
    ): List<TaxModel> {
        val user = userHolder.get() ?: return emptyList()

        val taxes = koki.taxes(
            ids = emptyList(),
            taxTypeIds = emptyList(),
            accountIds = listOf(user.account.id),
            participantIds = emptyList(),
            assigneeIds = emptyList(),
            createdByIds = emptyList(),
            statuses = emptyList(),
            fiscalYear = null,
            startAtFrom = null,
            startAtTo = null,
            dueAtFrom = null,
            dueAtTo = null,
            limit = limit,
            offset = offset,
        ).taxes

        // Types
        val taxTypeIds = taxes.mapNotNull { tax -> tax.taxTypeId }.toSet()
        val taxTypes = typeService.types(
            ids = taxTypeIds.toList(),
            limit = taxTypeIds.size
        ).associateBy { taxType -> taxType.id }

        return taxes.map { tax ->
            mapper.toTax(
                entity = tax,
                account = user.account,
                taxType = taxTypes[tax.taxTypeId]
            )
        }
    }
}
