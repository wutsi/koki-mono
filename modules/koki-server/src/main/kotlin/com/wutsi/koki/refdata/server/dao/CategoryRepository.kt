package com.wutsi.koki.refdata.server.dao

import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.server.domain.CategoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : CrudRepository<CategoryEntity, Long> {
    fun findByType(type: CategoryType): List<CategoryEntity>
}
