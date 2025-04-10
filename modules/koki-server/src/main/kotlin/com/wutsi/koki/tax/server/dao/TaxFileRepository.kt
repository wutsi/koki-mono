package com.wutsi.koki.tax.server.dao

import com.wutsi.koki.tax.server.domain.TaxFileEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TaxFileRepository : CrudRepository<TaxFileEntity, Long>
