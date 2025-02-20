package com.wutsi.koki.refdata.server.dao

import com.wutsi.koki.refdata.server.domain.JuridictionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JuridictionRepository : CrudRepository<JuridictionEntity, Long> {
    fun findByCountry(country: String): List<JuridictionEntity>
}
