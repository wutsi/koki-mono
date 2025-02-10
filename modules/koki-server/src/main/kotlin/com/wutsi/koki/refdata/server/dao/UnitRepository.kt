package com.wutsi.koki.product.server.dao

import com.wutsi.koki.product.server.domain.UnitEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UnitRepository : CrudRepository<UnitEntity, Long>
