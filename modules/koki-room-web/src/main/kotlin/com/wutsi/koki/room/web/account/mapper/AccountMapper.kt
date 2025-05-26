package com.wutsi.koki.room.web.account.mapper

import com.wutsi.koki.account.dto.Account
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.room.web.account.model.AccountModel
import com.wutsi.koki.room.web.common.mapper.TenantAwareMapper
import com.wutsi.koki.room.web.refdata.mapper.LocationMapper
import com.wutsi.koki.room.web.refdata.model.LocationModel
import com.wutsi.koki.room.web.tenant.model.TypeModel
import com.wutsi.koki.room.web.user.model.UserModel
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class AccountMapper(
    private val locationMapper: LocationMapper,
) : TenantAwareMapper() {
    fun toAccountModel(
        entity: AccountSummary,
        accountTypes: Map<Long, TypeModel>,
        users: Map<Long, UserModel>
    ): AccountModel {
        val fmt = createDateTimeFormat()
        return AccountModel(
            id = entity.id,
            accountType = entity.accountTypeId?.let { id -> accountTypes[id] },
            name = entity.name,
            phone = entity.phone,
            email = entity.email,
            mobile = entity.mobile,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
            managedBy = entity.managedById?.let { id -> users[id] },
        )
    }

    fun toAccountModel(
        entity: Account,
        accountTypes: Map<Long, TypeModel>,
        users: Map<Long, UserModel>,
        locations: Map<Long, LocationModel>,
    ): AccountModel {
        val fmt = createDateTimeFormat()
        return AccountModel(
            id = entity.id,
            accountType = entity.accountTypeId?.let { id -> accountTypes[id] },
            name = entity.name,
            phone = entity.phone,
            email = entity.email,
            mobile = entity.mobile,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
            managedBy = entity.managedById?.let { id -> users[id] },
            description = entity.description,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayName },
            website = entity.website,
            shippingAddress = entity.shippingAddress?.let { address ->
                locationMapper.toAddressModel(address, locations)
            },
            billingAddress = entity.billingAddress?.let { address ->
                locationMapper.toAddressModel(address, locations)
            },
            billingSameAsShippingAddress = entity.billingSameAsShippingAddress,
        )
    }
}
