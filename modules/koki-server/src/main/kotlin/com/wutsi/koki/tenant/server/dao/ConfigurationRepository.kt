package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigurationRepository : CrudRepository<ConfigurationEntity, Long>
