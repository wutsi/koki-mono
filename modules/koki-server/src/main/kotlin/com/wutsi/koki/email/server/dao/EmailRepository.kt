package com.wutsi.koki.email.dao

import com.wutsi.koki.email.domain.EmailEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailRepository : CrudRepository<EmailEntity, String>
