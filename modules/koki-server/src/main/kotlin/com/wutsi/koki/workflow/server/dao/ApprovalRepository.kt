package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.ApprovalEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ApprovalRepository : CrudRepository<ApprovalEntity, Long>
