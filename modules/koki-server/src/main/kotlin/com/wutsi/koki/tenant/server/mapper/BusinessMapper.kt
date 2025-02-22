package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.tenant.dto.Business
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import org.springframework.stereotype.Service

@Service
class BusinessMapper {
    fun toBusiness(entity: BusinessEntity) = Business(
        id = entity.id!!,
        companyName = entity.companyName,
        phone = entity.phone,
        email = entity.email,
        fax = entity.fax,
        website = entity.website,
        juridictionIds = entity.juridictions.mapNotNull { juridiction -> juridiction.id },
        address = if (entity.hasAddress()) {
            Address(
                street = entity.addressStreet,
                country = entity.addressCountry,
                stateId = entity.addressStateId,
                cityId = entity.addressCityId,
                postalCode = entity.addressPostalCode
            )
        } else {
            null
        },
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt,
        createdById = entity.modifiedById,
        modifiedById = entity.modifiedById,
    )
}
