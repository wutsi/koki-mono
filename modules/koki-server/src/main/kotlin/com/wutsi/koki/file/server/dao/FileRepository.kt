package com.wutsi.koki.file.server.dao

import com.wutsi.koki.file.server.domain.FileEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : CrudRepository<FileEntity, Long>
