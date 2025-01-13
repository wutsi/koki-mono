package com.wutsi.koki.portal.contact.mapper

import com.wutsi.koki.contact.dto.Contact
import com.wutsi.koki.contact.dto.ContactSummary
import com.wutsi.koki.contact.dto.ContactType
import com.wutsi.koki.contact.dto.ContactTypeSummary
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.contact.model.ContactTypeModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class ContactMapper : TenantAwareMapper() {
    fun toContactModel(
        entity: ContactSummary,
        users: Map<Long, UserModel>,
        account: AccountModel?,
        contactType: ContactTypeModel?,
    ): ContactModel {
        val fmt = createDateFormat()
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
        contactType: ContactTypeModel?,
    ): ContactModel {
        val fmt = createDateFormat()
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
        )
    }

    fun toContactTypeModel(entity: ContactType): ContactTypeModel {
        return ContactTypeModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: entity.name,
            active = entity.active,
            description = entity.description,
        )
    }

    fun toContactTypeModel(entity: ContactTypeSummary): ContactTypeModel {
        return ContactTypeModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: entity.name,
            active = entity.active,
        )
    }
}
