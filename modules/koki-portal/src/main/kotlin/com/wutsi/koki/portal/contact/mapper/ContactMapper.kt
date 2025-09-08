package com.wutsi.koki.portal.contact.mapper

import com.wutsi.koki.contact.dto.Contact
import com.wutsi.koki.contact.dto.ContactSummary
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.refdata.dto.Address
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class ContactMapper : TenantAwareMapper() {
    fun toContactModel(
        entity: ContactSummary,
        users: Map<Long, UserModel>,
        account: AccountModel?,
        contactType: TypeModel?,
    ): ContactModel {
        val fmt = createDateTimeFormat()
        return ContactModel(
            id = entity.id,
            account = account,
            contactType = contactType,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
            firstName = entity.firstName,
            lastName = entity.lastName,
            phone = entity.phone,
            mobile = entity.mobile,
            phoneFormatted = entity.phone?.let { number -> formatPhoneNumber(number) },
            mobileFormatted = entity.mobile?.let { number -> formatPhoneNumber(number) },
            email = entity.email,
        )
    }

    fun toContactModel(
        entity: Contact,
        users: Map<Long, UserModel>,
        account: AccountModel?,
        contactType: TypeModel?,
        locations: Map<Long, LocationModel>
    ): ContactModel {
        val fmt = createDateTimeFormat()
        return ContactModel(
            id = entity.id,
            account = account,
            contactType = contactType,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
            firstName = entity.firstName,
            lastName = entity.lastName,
            phone = entity.phone,
            mobile = entity.mobile,
            phoneFormatted = entity.phone?.let { number -> formatPhoneNumber(number) },
            mobileFormatted = entity.mobile?.let { number -> formatPhoneNumber(number) },
            email = entity.email,
            profession = entity.profession,
            employer = entity.employer,
            gender = entity.gender,
            salutation = entity.salutation,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayName },
            preferredCommunicationMethod = entity.preferredCommunicationMethod,
            address = toAddress(entity.address, locations)
            )
    }

    private fun toAddress(address: Address?, locations: Map<Long, LocationModel>): AddressModel? {
        address ?: return null
        return AddressModel(
            country = address.country,
            city = address.cityId?.let { id -> locations[id] },
            neighbourhood = address.neighborhoodId?.let { id -> locations[id] },
            state = address.stateId?.let { id -> locations[id] },
            street = address.street,
            postalCode = address.postalCode,
            countryName = address.country?.let { country ->
                Locale(LocaleContextHolder.getLocale().language, country).getDisplayCountry()
            }
        )
    }
}
