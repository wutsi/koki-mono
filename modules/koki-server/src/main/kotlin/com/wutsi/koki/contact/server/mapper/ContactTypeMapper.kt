package com.wutsi.koki.contact.server.mapper

import com.wutsi.koki.contact.dto.ContactType
import com.wutsi.koki.contact.dto.ContactTypeSummary
import com.wutsi.koki.contact.server.domain.ContactTypeEntity
import org.springframework.stereotype.Service

@Service
class ContactTypeMapper {
    fun toContactType(entity: ContactTypeEntity): ContactType {
        return ContactType(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toContactTypeSummary(entity: ContactTypeEntity): ContactTypeSummary {
        return ContactTypeSummary(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
