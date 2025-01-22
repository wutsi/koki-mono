package com.wutsi.koki.email.server.dao

import com.wutsi.koki.email.server.domain.AttachmentEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepository : CrudRepository<AttachmentEntity, Long> {
    fun findByEmailId(emailId: String): List<AttachmentEntity>
}
