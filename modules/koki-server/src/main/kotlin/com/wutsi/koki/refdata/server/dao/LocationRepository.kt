package com.wutsi.koki.refdata.server.dao

import com.wutsi.koki.refdata.server.domain.UnitEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UnitRepository : CrudRepository<UnitEntity, Long>
