package com.wutsi.koki.portal.account.mapper

import com.wutsi.koki.account.dto.Account
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.account.dto.Attribute
import com.wutsi.koki.account.dto.AttributeSummary
import com.wutsi.koki.portal.account.model.AccountAttributeModel
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.model.AttributeModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.refdata.mapper.RefDataMapper
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class AccountMapper(
    private val refDataMapper: RefDataMapper,
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
            mobile = entity.mobile,
            phoneFormatted = entity.phone?.let { number -> formatPhoneNumber(number) },
            mobileFormatted = entity.mobile?.let { number -> formatPhoneNumber(number) },
            email = entity.email,
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
    ): AccountModel {
        val fmt = createDateTimeFormat()
        return AccountModel(
            id = entity.id,
            accountType = entity.accountTypeId?.let { id -> accountTypes[id] },
            name = entity.name,
            email = entity.email,
            phone = entity.phone,
            mobile = entity.mobile,
            phoneFormatted = entity.phone?.let { number -> formatPhoneNumber(number, entity.billingAddress?.country) },
            mobileFormatted = entity.mobile?.let { number ->
                formatPhoneNumber(
                    number,
                    entity.billingAddress?.country
                )
            },
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
}
