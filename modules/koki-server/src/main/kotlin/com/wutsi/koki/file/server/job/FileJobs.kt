package com.wutsi.koki.file.server.job

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.TenantStatus
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.TenantService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/files/jobs")
@Service
class FileJobs(
    private val service: FileService,
    private val publisher: Publisher,
    private val tenantService: TenantService,
) {
    @PostMapping("/under-review")
    @Operation(summary = "Process all files UNDER_REVIEW")
    @Scheduled(cron = "\${koki.module.file.cron.under-review}")
    fun underReview() {
        tenantService.all().forEach { tenant ->
            if (tenant.status == TenantStatus.ACTIVE) {
                underReview(tenant)
            }
        }
    }

    private fun underReview(tenant: TenantEntity) {
        val logger = DefaultKVLogger()
        try {
            logger.add("job", "FileJobs#underReview")
            logger.add("tenant_id", tenant.id)

            service.search(
                tenantId = tenant.id ?: -1,
                status = FileStatus.UNDER_REVIEW,
                limit = Integer.MAX_VALUE,
            ).forEach { file ->
                publisher.publish(
                    FileUploadedEvent(
                        fileId = file.id ?: -1,
                        tenantId = file.tenantId,
                        fileType = file.type,
                        owner = file.ownerId?.let { id ->
                            file.ownerType?.let { type -> ObjectReference(id, type) }
                        },
                    )
                )
            }
        } catch (ex: Exception) {
            logger.setException(ex)
        } finally {
            logger.log()
        }
    }
}
