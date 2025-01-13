package com.wutsi.koki.portal.account.mapper

import com.wutsi.koki.account.dto.Account
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.account.dto.AccountType
import com.wutsi.koki.account.dto.AccountTypeSummary
import com.wutsi.koki.account.dto.Attribute
import com.wutsi.koki.account.dto.AttributeSummary
import com.wutsi.koki.portal.account.model.AccountAttributeModel
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.model.AccountTypeModel
import com.wutsi.koki.portal.account.model.AttributeModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class AccountMapper : TenantAwareMapper() {
    fun toAccountModel(
        entity: AccountSummary,
        accountTypes: Map<Long, AccountTypeModel>,
        users: Map<Long, UserModel>
    ): AccountModel {
        val fmt = createDateFormat()
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
        accountTypes: Map<Long, AccountTypeModel>,
        users: Map<Long, UserModel>,
        attributes: Map<Long, AttributeModel>
    ): AccountModel {
        val fmt = createDateFormat()
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
            attributes = entity.attributes.mapNotNull { entry ->
                val attribute = attributes[entry.key]
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

    fun toAccountTypeModel(entity: AccountType): AccountTypeModel {
        return AccountTypeModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: entity.name,
            description = entity.description,
            active = entity.active,
        )
    }

    fun toAccountTypeModel(entity: AccountTypeSummary): AccountTypeModel {
        return AccountTypeModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: entity.name,
            active = entity.active,
        )
    }

    fun toAttributeModel(entity: Attribute): AttributeModel {
        val fmt = createDateFormat()
        return AttributeModel(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            label = entity.label ?: entity.name,
            required = entity.required,
            active = entity.active,
            choices = entity.choices,
            description = entity.description,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.createdAt),
        )
    }

    fun toAttributeModel(entity: AttributeSummary): AttributeModel {
        val fmt = createDateFormat()
        return AttributeModel(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            label = entity.label ?: entity.name,
            required = entity.required,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.createdAt),
        )
    }
}
