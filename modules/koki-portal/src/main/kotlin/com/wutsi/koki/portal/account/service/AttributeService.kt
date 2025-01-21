package com.wutsi.koki.portal.account.service

import com.wutsi.koki.account.dto.Attribute
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.portal.account.mapper.AccountMapper
import com.wutsi.koki.portal.account.model.AttributeModel
import com.wutsi.koki.sdk.KokiAccounts
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AttributeService(
    private val koki: KokiAccounts,
    private val mapper: AccountMapper,
) {
    fun attribute(id: Long): Attribute {
        return koki.attribute(id).attribute
    }

    fun attributes(
        ids: List<Long> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<AttributeModel> {
        val attributes = koki.attributes(
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset
        ).attributes

        return attributes.map { attribute -> mapper.toAttributeModel(attribute) }
    }

    fun upload(file: MultipartFile): ImportResponse {
        return koki.uploadAttributes(file)
    }
}
