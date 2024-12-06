package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.LogEntryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LogEntryRepository : CrudRepository<LogEntryEntity, String>
