package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.server.io.WorkflowPNGExporter
import com.wutsi.koki.workflow.server.service.WorkflowService
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
class ExportWorkflowPNGEndpoint(
    private val exporter: WorkflowPNGExporter,
    private val service: WorkflowService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ExportWorkflowPNGEndpoint::class.java)
    }

    @GetMapping("/v1/workflows/image/{tenant-id}-{workflow-id}.png")
    fun update(
        @PathVariable("tenant-id") tenantId: Long,
        @PathVariable("workflow-id") id: Long,
        response: HttpServletResponse
    ) {
        response.contentType = "image/png"
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename("$tenantId-$id.png").build().toString()
        )

        try {
            val workflow = service.get(id, tenantId)
            exporter.export(workflow, response.outputStream)
        } catch (ex: NotFoundException) {
            LOGGER.warn("workflow not found", ex)
            response.status = 404
        }
    }
}
