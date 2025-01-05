package com.wutsi.koki.portal.account.mapper

import com.wutsi.koki.account.dto.Account
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.portal.account.model.AccountAttributeModel
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.model.AttributeModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.model.UserModel
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class AccountMapper : TenantAwareMapper() {
    fun toAccountModel(entity: AccountSummary, users: Map<Long, UserModel>): AccountModel {
        val fmt = createDateFormat()
        return AccountModel(
            id = entity.id,
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
        users: Map<Long, UserModel>,
        attributeMap: Map<Long, AttributeModel>
    ): AccountModel {
        val fmt = createDateFormat()
        return AccountModel(
            id = entity.id,
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
            attributes = entity.attributes.mapNotNull { entry ->
                val attribute = attributeMap[entry.key]
                if (attribute == null) {
                    null
                } else {
                    AccountAttributeModel(
                        attribute = attribute,
                        value = entity.attributes[entry.key] ?: ""
                    )
                }
            }
        )
    }
}
