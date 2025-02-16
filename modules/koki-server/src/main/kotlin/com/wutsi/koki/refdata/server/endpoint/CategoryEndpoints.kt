package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.dto.SearchCategoryResponse
import com.wutsi.koki.refdata.server.io.CategoryImporter
import com.wutsi.koki.refdata.server.mapper.CategoryMapper
import com.wutsi.koki.refdata.server.service.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/categories")
class CategoryEndpoints(
    val importer: CategoryImporter,
    val mapper: CategoryMapper,
    val service: CategoryService,
) {
    @GetMapping("/import")
    fun import(@RequestParam type: CategoryType): ImportResponse {
        return importer.import(type)
    }

    @GetMapping
    fun search(
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "parent-id") parentId: Long? = null,
        @RequestParam(required = false) type: CategoryType? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) level: Int? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchCategoryResponse {
        val categories = service.search(
            keyword = keyword,
            ids = ids,
            parentId = parentId,
            type = type,
            active = active,
            level = level,
            limit = limit,
            offset = offset,
        )
        return SearchCategoryResponse(
            categories = categories.map { category -> mapper.toCategory(category) }
        )
    }
}
