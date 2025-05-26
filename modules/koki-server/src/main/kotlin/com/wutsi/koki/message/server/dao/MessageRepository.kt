package com.wutsi.koki.message.server.dao

import com.wutsi.koki.message.server.domain.MessageEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : CrudRepository<MessageEntity, Long>
