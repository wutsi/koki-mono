package com.wutsi.koki.portal.common.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import org.springframework.stereotype.Service

@Service
class ObjectReferenceService {
    fun reference(id: Long, type: ObjectType): ObjectReferenceModel {
        return ObjectReferenceModel(id = id, type = type)
    }

    fun references(ids: List<Long>, type: ObjectType): List<ObjectReferenceModel> {
        return ids.map { id -> ObjectReferenceModel(id = id, type = type) }
    }
}
