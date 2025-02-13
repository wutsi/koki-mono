package com.wutsi.koki.account.server.mapper

import com.wutsi.koki.account.dto.Account
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.common.dto.Address
import com.wutsi.koki.form.server.domain.AccountEntity
import org.springframework.stereotype.Service

@Service
class AccountMapper {
    fun toAccount(entity: AccountEntity): Account {
        return Account(
            id = entity.id ?: -1,
            accountTypeId = entity.accountTypeId,
            name = entity.name,
            description = entity.description,
            phone = entity.phone,
            email = entity.email,
            mobile = entity.mobile,
            website = entity.website,
            language = entity.language,
            modifiedAt = entity.modifiedAt,
            modifiedById = entity.modifiedById,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            managedById = entity.managedById,
            attributes = entity.accountAttributes.mapNotNull { accountAttribute ->
                if (accountAttribute.value.isNullOrEmpty()) {
                    null
                } else {
                    accountAttribute.attributeId to accountAttribute.value!!
                }
            }.toMap(),
            shippingAddress = if (entity.hasShippingAddress()){
                Address(
                    street = entity.shippingStreet,
                    postalCode = entity.shippingPostalCode,
                    cityId = entity.shippingCityId,
                )
            } else {
                null
            },
            billingAddress = if (entity.hasBillingAddress()){
                Address(
                    street = entity.billingStreet,
                    postalCode = entity.billingPostalCode,
                    cityId = entity.billingCityId,
                )
            } else {
                null
            },
        )
    }

    fun toAccountSummary(entity: AccountEntity): AccountSummary {
        return AccountSummary(
            id = entity.id ?: -1,
            accountTypeId = entity.accountTypeId,
            name = entity.name,
            phone = entity.phone,
            email = entity.email,
            mobile = entity.mobile,
            modifiedAt = entity.modifiedAt,
            modifiedById = entity.modifiedById,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            managedById = entity.managedById,
        )
    }
}
