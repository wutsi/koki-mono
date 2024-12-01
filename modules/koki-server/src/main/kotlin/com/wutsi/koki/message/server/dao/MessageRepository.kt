package com.wutsi.koki.message.server.dao

import com.wutsi.koki.message.server.domain.MessageEntity
import org.springframework.data.repository.CrudRepository

interface MessageRepository : CrudRepository<MessageEntity, String> {
    fun findByNameIgnoreCaseAndTenantId(name: String, tenantId: Long): MessageEntity?
}
