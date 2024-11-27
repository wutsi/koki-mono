package com.wutsi.koki.file.server.dao

import com.wutsi.koki.file.server.domain.FileEntity
import org.springframework.data.repository.CrudRepository

interface FileRepository : CrudRepository<FileEntity, String>
