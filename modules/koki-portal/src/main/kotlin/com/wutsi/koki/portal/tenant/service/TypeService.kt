package com.wutsi.koki.portal.contact.service

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.portal.contact.mapper.ContactMapper
import com.wutsi.koki.portal.contact.model.ContactTypeModel
import com.wutsi.koki.sdk.KokiContacts
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ContactTypeService(
    private val koki: KokiContacts,
    private val mapper: ContactMapper,
) {
    fun contactType(id: Long): ContactTypeModel {
        val contactType = koki.type(id).contactType
        return mapper.toContactTypeModel(contactType)
    }

    fun contactTypes(
        ids: List<Long> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<ContactTypeModel> {
        val contactTypes = koki.types(
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset
        ).contactTypes

        return contactTypes.map { contactType -> mapper.toContactTypeModel(contactType) }
    }

    fun upload(file: MultipartFile): ImportResponse {
        return koki.uploadTypes(file)
    }
}
