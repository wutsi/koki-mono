package com.wutsi.koki.portal.client.account

import org.springframework.stereotype.Service

@Service
class AccountMapper {
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
        attributes: Map<Long, AttributeModel>,
        locations: Map<Long, LocationModel>,
        accountUser: AccountUserModel?,
        invitation: InvitationModel?,
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
            },
            shippingAddress = entity.shippingAddress?.let { address ->
                refDataMapper.toAddressModel(address, locations)
            },
            billingAddress = entity.billingAddress?.let { address ->
                refDataMapper.toAddressModel(address, locations)
            },
            billingSameAsShippingAddress = entity.billingSameAsShippingAddress,
            accountUser = accountUser,
            invitation = invitation,
        )
    }

    fun toAttributeModel(entity: Attribute): AttributeModel {
        val fmt = createDateTimeFormat()
        return AttributeModel(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            label = entity.label ?: entity.name,
            required = entity.required,
            active = entity.active,
            choices = entity.choices,
            description = entity.description?.trim()?.ifEmpty { null },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.createdAt),
        )
    }

    fun toAttributeModel(entity: AttributeSummary): AttributeModel {
        val fmt = createDateTimeFormat()
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

    fun toAccountUserModel(entity: AccountUser): AccountUserModel {
        val fmt = createDateTimeFormat()
        return AccountUserModel(
            id = entity.id,
            username = entity.username,
            status = entity.status,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    fun toInvitationModel(entity: Invitation): InvitationModel {
        val fmt = createDateTimeFormat()
        return InvitationModel(
            id = entity.id,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
        )
    }
}
