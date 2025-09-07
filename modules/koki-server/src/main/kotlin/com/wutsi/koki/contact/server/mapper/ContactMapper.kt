package com.wutsi.koki.contact.server.mapper

import com.wutsi.koki.contact.dto.Contact
import com.wutsi.koki.contact.dto.ContactSummary
import com.wutsi.koki.contact.server.domain.ContactEntity
import org.springframework.stereotype.Service

@Service
class ContactMapper {
    fun toContact(entity: ContactEntity): Contact {
        return Contact(
            id = entity.id!!,
            accountId = entity.account?.id,
            contactTypeId = entity.contactTypeId,
            firstName = entity.firstName,
            lastName = entity.lastName,
            phone = entity.phone,
            mobile = entity.mobile,
            email = entity.email,
            gender = entity.gender,
            profession = entity.profession,
            employer = entity.employer,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
            salutation = entity.salutation,
            language = entity.language,
        )
    }

    fun toContactSummary(entity: ContactEntity): ContactSummary {
        return ContactSummary(
            id = entity.id!!,
            accountId = entity.account?.id ?: -1,
            contactTypeId = entity.contactTypeId,
            firstName = entity.firstName,
            lastName = entity.lastName,
            phone = entity.phone,
            mobile = entity.mobile,
            email = entity.email,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
        )
    }
}
