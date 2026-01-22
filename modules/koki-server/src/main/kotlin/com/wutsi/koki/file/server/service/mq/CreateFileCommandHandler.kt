package com.wutsi.koki.file.server.service.mq

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.WutsiException
import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CreateFileCommandHandler(
    private val fileService: FileService,
    private val publisher: Publisher,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CreateFileCommandHandler::class.java)
    }

    fun handle(command: CreateFileCommand): Boolean {
        logger.add("command_url", command.url)
        logger.add("command_tenant_id", command.tenantId)
        logger.add("command_owner_id", command.owner?.id)
        logger.add("command_owner_type", command.owner?.type)

        try {
            val file = fileService.create(
                request = CreateFileRequest(
                    url = command.url,
                    owner = command.owner,
                ),
                tenantId = command.tenantId,
            )
            logger.add("file_id", file.id)

            publisher.publish(
                FileUploadedEvent(
                    fileId = file.id ?: -1,
                    fileType = file.type,
                    tenantId = file.tenantId,
                    owner = command.owner,
                )
            )
            return true
        } catch (ex: WutsiException) {
            if (ex.error.code == ErrorCode.FILE_ALREADY_EXISTS) {
                logger.add("file_already_exists", true)
                LOGGER.warn("File already exists: ${command.url} for tenant=${command.tenantId}", ex)
                return false // Ignore duplicate file
            } else {
                throw ex
            }
        }
    }
}
