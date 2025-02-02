package com.wutsi.koki.portal.tenant.service

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.tenant.mapper.TenantMapper
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.sdk.KokiTypes
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class TypeService(
    private val koki: KokiTypes,
    private val mapper: TenantMapper,
) {
    fun type(id: Long): TypeModel {
        val type = koki.type(id).type
        return mapper.toTypeModel(type)
    }

    fun types(
        ids: List<Long> = emptyList(),
        keyword: String? = null,
        active: Boolean? = null,
        objectType: ObjectType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<TypeModel> {
        val types = koki.types(
            ids = ids,
            keyword = keyword,
            objectType = objectType,
            active = active,
            limit = limit,
            offset = offset
        ).types.sortedBy { type -> type.title }
        return types.map { type -> mapper.toTypeModel(type) }
    }

    fun upload(file: MultipartFile, objectType: ObjectType): ImportResponse {
        return koki.uploadTypes(file, objectType)
    }
}
