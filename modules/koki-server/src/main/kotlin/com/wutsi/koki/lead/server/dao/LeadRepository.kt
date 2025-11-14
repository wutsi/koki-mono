package com.wutsi.koki.lead.server.dao

import com.wutsi.koki.lead.server.domain.LeadEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LeadRepository : CrudRepository<LeadEntity, Long>
