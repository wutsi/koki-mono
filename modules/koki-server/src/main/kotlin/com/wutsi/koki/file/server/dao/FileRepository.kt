package com.wutsi.koki.document.server.dao

import com.wutsi.koki.document.server.domain.FileEntity
import org.springframework.data.repository.CrudRepository

interface FileRepository : CrudRepository<FileEntity, String>
