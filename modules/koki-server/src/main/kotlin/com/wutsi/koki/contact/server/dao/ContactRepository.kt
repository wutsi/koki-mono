package com.wutsi.koki.contact.server.dao

import com.wutsi.koki.contact.server.domain.ContactEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactRepository : CrudRepository<ContactEntity, Long>
