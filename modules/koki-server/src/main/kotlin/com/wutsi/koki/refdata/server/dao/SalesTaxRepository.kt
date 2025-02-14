package com.wutsi.koki.refdata.server.dao

import com.wutsi.koki.refdata.server.domain.SalesTaxEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SalesTaxRepository : CrudRepository<SalesTaxEntity, Long> {
    fun findByCountry(country: String): List<SalesTaxEntity>
}
