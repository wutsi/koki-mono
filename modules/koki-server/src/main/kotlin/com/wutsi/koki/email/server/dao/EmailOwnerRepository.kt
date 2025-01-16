package com.wutsi.koki.email.server.dao

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.server.domain.EmailOwnerEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailOwnerRepository : CrudRepository<EmailOwnerEntity, String> {
    fun findByEmailId(id: String): List<EmailOwnerEntity>

    fun findByOwnerIdAndOwnerType(ownerId: Long, ownerType: ObjectType): List<EmailOwnerEntity>
}
