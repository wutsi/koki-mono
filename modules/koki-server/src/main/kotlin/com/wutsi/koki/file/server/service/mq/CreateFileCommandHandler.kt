package com.wutsi.koki.file.server.service.mq

import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.stereotype.Service

@Service
class CreateFileCommandHandler(
    private val fileService: FileService,
    private val publisher: Publisher,
    private val logger: KVLogger,
) {
    fun handle(command: CreateFileCommand): FileEntity {
        logger.add("command_url", command.url)
        logger.add("command_tenant_id", command.tenantId)
        logger.add("command_owner_id", command.owner?.id)
        logger.add("command_owner_type", command.owner?.type)

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
        return file
    }
}
