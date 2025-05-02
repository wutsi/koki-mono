package com.wutsi.koki.refdata.server.dao

import com.wutsi.koki.refdata.server.domain.AmenityEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AmenityRepository : CrudRepository<AmenityEntity, Long>
