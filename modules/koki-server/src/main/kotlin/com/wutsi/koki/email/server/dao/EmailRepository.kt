package com.wutsi.koki.email.server.dao

import com.wutsi.koki.email.server.domain.EmailEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailRepository : CrudRepository<EmailEntity, String>
