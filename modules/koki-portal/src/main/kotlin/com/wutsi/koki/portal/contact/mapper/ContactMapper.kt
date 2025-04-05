package com.wutsi.koki.portal.contact.mapper

import com.wutsi.koki.contact.dto.Contact
import com.wutsi.koki.contact.dto.ContactSummary
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.portal.user.model.UserModel
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
            email = entity.email,
        )
    }

    fun toContactModel(
        entity: Contact,
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
            email = entity.email,
            profession = entity.profession,
            employer = entity.employer,
            gender = entity.gender,
            salutation = entity.salutation,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayName },
        )
    }
}
