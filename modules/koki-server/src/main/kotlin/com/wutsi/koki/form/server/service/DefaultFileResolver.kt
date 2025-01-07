package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.service.FileService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultFileResolver(private val fileService: FileService) : FileResolver {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultFileResolver::class.java)
    }

    override fun resolve(id: String, tenantId: Long): File? {
        try {
            val file = fileService.get(id.toLong(), tenantId)
            return File(
                name = file.name,
                contentLength = file.contentLength,
                contentType = file.contentType,
            )
        } catch (ex: NotFoundException) {
            LOGGER.warn("Unable to resolve $id", ex)
            return null
        }
    }
}
