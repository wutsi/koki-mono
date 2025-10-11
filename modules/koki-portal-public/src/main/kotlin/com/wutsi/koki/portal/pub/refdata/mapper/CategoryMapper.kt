package com.wutsi.koki.portal.pub.refdata.mapper

import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.refdata.model.CategoryModel
import com.wutsi.koki.refdata.dto.Category
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class CategoryMapper : TenantAwareMapper() {
    fun toCategoryModel(entity: Category): CategoryModel {
        val language = LocaleContextHolder.getLocale().language
        return CategoryModel(
            id = entity.id,
            parentId = entity.parentId,
            type = entity.type,
            level = entity.level,
            active = entity.active,
            name = when (language) {
                "fr" -> entity.nameFr ?: entity.name
                else -> entity.name
            },
            longName = when (language) {
                "fr" -> entity.longNameFr ?: entity.longName
                else -> entity.longName
            },
        )
    }
}
