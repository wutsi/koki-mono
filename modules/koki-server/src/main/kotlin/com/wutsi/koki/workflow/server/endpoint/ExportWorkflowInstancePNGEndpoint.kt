package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.io.WorkflowPNGExporter
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ExportWorkflowInstancePNGEndpoint(
    private val exporter: WorkflowPNGExporter,
    private val service: WorkflowInstanceService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ExportWorkflowPNGEndpoint::class.java)
    }

    @GetMapping("/v1/workflow-instances/images/{tenant-id}.{workflow-instance-id}.png")
    fun update(
        @PathVariable("tenant-id") tenantId: Long,
        @PathVariable("workflow-instance-id") id: String,
        response: HttpServletResponse
    ) {
        response.contentType = "image/png"
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename("$tenantId.$id.png").build().toString()
        )

        try {
            val workflowInstance = service.get(id, tenantId)
            val runningActivityNames = filterActivityNames(WorkflowStatus.RUNNING, workflowInstance)
            val doneActivityNames = filterActivityNames(WorkflowStatus.DONE, workflowInstance)

            exporter.export(workflowInstance.workflow, response.outputStream, runningActivityNames, doneActivityNames)
        } catch (ex: NotFoundException) {
            LOGGER.warn("workflow not found", ex)
            response.status = 404
        }
    }

    private fun filterActivityNames(status: WorkflowStatus, workflowInstance: WorkflowInstanceEntity): List<String> {
        return workflowInstance.activityInstances
            .filter { activityInstance -> activityInstance.status == status }
            .map { activityInstance -> activityInstance.activity.name }
    }
}
