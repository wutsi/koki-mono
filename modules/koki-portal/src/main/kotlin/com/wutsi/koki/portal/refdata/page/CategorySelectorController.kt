package com.wutsi.koki.portal.refdata.page

import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.refdata.dto.CategoryType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CategorySelectorController(private val service: CategoryService) {
    @GetMapping("/categories/selector/search")
    fun search(
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam type: CategoryType,
    ): List<Map<String, Any>> {
        val categories = service.categories(
            keyword = keyword,
            type = type,
            active = true,
            limit = 20,
        )

        return categories.map { category ->
            mapOf(
                "id" to category.id,
                "name" to category.longName
            )
        }
    }
}
