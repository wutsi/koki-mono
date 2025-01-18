package com.wutsi.koki.note.server.dao

import com.wutsi.koki.module.server.domain.ModuleEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ModuleRepository : CrudRepository<ModuleEntity, Long>
