package com.wutsi.koki.portal.tax.mapper

import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.tax.model.TaxModel
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.tax.dto.Tax
import com.wutsi.koki.tax.dto.TaxSummary
import org.springframework.stereotype.Service

@Service
class TaxMapper : TenantAwareMapper() {
    fun toTax(
        entity: TaxSummary,
        taxType: TypeModel?,
        account: AccountModel,
        users: Map<Long, UserModel?>
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
            dueAt = entity.dueAt,
            dueAtText = entity.dueAt?.let { date -> dateFormat.format(date) },
            createdBy = entity.createdById?.let { id -> users[id] },
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            accountant = entity.accountantId?.let { id -> users[id] },
            technician = entity.technicianId?.let { id -> users[id] },
            assignee = entity.assigneeId?.let { id -> users[id] },
            account = account,
        )
    }

    fun toTax(
        entity: Tax,
        taxType: TypeModel?,
        account: AccountModel,
        users: Map<Long, UserModel?>
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
            dueAt = entity.dueAt,
            dueAtText = entity.dueAt?.let { date -> dateFormat.format(date) },
            createdBy = entity.createdById?.let { id -> users[id] },
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            accountant = entity.accountantId?.let { id -> users[id] },
            technician = entity.technicianId?.let { id -> users[id] },
            assignee = entity.assigneeId?.let { id -> users[id] },
            account = account,
            description = entity.description,
        )
    }
}
