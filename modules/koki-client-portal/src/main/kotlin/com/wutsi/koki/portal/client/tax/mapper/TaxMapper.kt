package com.wutsi.koki.portal.client.tax.mapper

import com.wutsi.koki.portal.client.account.model.AccountModel
import com.wutsi.koki.portal.client.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.client.tax.model.TaxModel
import com.wutsi.koki.portal.client.tenant.model.TypeModel
import com.wutsi.koki.tax.dto.Tax
import com.wutsi.koki.tax.dto.TaxSummary
import org.springframework.stereotype.Service

@Service
class TaxMapper : TenantAwareMapper() {
    fun toTax(
        entity: TaxSummary,
        taxType: TypeModel?,
        account: AccountModel,
    ): TaxModel {
        val fmt = createDateTimeFormat()
        val dateFormat = createDateFormat()
        return TaxModel(
            id = entity.id,
            taxType = taxType,
            fiscalYear = entity.fiscalYear,
            status = entity.status,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            startAt = entity.startAt,
            startAtText = entity.startAt?.let { date -> dateFormat.format(date) },
            account = account,
        )
    }

    fun toTax(
        entity: Tax,
        taxType: TypeModel?,
        account: AccountModel,
    ): TaxModel {
        val fmt = createDateTimeFormat()
        val dateFormat = createDateFormat()
        return TaxModel(
            id = entity.id,
            taxType = taxType,
            fiscalYear = entity.fiscalYear,
            status = entity.status,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            startAt = entity.startAt,
            startAtText = entity.startAt?.let { date -> dateFormat.format(date) },
            account = account,
            description = entity.description,
        )
    }
}
