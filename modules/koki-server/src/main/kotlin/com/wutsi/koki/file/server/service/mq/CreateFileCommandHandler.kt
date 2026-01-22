package com.wutsi.koki.file.server.service.mq

import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.stereotype.Service

@Service
class CreateFileCommandHandler(
    private val fileService: FileService,
    private val publisher: Publisher
) {
    fun handle(command: CreateFileCommand) {
        val file = fileService.create(
            request = CreateFileRequest(
                url = command.url,
                owner = command.owner,
            ),
            tenantId = command.tenantId,
        )
        publisher.publish(
            FileUploadedEvent(
                fileId = file.id ?: -1,
                tenantId = file.tenantId,
            )
        )
    }
}
