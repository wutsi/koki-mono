package com.wutsi.koki.refdata.server.dao

import com.wutsi.koki.refdata.server.domain.SalesTaxEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SalesTaxRepository : CrudRepository<SalesTaxEntity, Long> {
    @Query("SELECT S FROM SalesTaxEntity S WHERE S.juridiction.country=?1")
    fun findByCountry(country: String): List<SalesTaxEntity>
}
