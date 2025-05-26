package com.wutsi.koki.room.web.tenant.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.room.web.tenant.mapper.TypeMapper
import com.wutsi.koki.room.web.tenant.model.TypeModel
import com.wutsi.koki.sdk.KokiTypes
import org.springframework.stereotype.Service

@Service
class TypeService(
    private val koki: KokiTypes,
    private val mapper: TypeMapper,
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
        ).types.sortedBy { type -> (type.title ?: type.name).uppercase() }
        return types.map { type -> mapper.toTypeModel(type) }
    }
}
