package com.wutsi.koki.portal.refdata.service

import com.wutsi.koki.portal.refdata.mapper.RefDataMapper
import com.wutsi.koki.portal.refdata.model.CategoryModel
import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.sdk.KokiRefData
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val koki: KokiRefData,
    private val mapper: RefDataMapper,
) {
    fun categories(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        parentId: Long? = null,
        type: CategoryType? = null,
        active: Boolean? = null,
        level: Int? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<CategoryModel> {
        val categories = koki.categories(
            keyword = keyword,
            ids = ids,
            parentId = parentId,
            type = type,
            level = level,
            active = active,
            limit = limit,
            offset = offset
        ).categories
        return categories.map { category -> mapper.toCategoryModel(category) }
    }

    fun category(id: Long): CategoryModel? {
        return categories(ids = listOf(id)).firstOrNull()
    }
}
