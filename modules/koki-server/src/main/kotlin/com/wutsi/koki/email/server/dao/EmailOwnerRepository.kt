package com.wutsi.koki.email.server.dao

import com.wutsi.koki.email.server.domain.EmailOwnerEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EmaiOwnerRepository : CrudRepository<EmailOwnerEntity, Long>
