package com.wutsi.koki.lead.server.dao

import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.lead.server.domain.LeadMessageEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LeadMessageRepository : CrudRepository<LeadMessageEntity, Long> {
    fun countByLeadAndIdIsLessThanEqual(lead: LeadEntity, id: Long): Long?
    fun countByLead(lead: LeadEntity): Long?
}
