package com.wutsi.koki.tax.server.dao

import com.wutsi.koki.tax.server.domain.TaxEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TaxRepository : CrudRepository<TaxEntity, Long>
