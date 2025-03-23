package com.wutsi.koki.file.server.dao

import com.wutsi.koki.file.server.domain.LabelEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LabelRepository : CrudRepository<LabelEntity, Long>{
    fun findByNameIn(names: List<String>): List<LabelEntity>
}
