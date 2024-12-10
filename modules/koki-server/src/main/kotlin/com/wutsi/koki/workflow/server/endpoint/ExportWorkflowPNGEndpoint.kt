package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.dto.ImportWorkflowRequest
import com.wutsi.koki.workflow.server.io.WorkflowExporter
import com.wutsi.koki.workflow.server.io.WorkflowPNGExporter
import com.wutsi.koki.workflow.server.service.WorkflowService
import com.wutsi.koki.workflow.server.validation.WorkflowValidator
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ExportWorkflowPNGEndpoint(
    private val pngExporter: WorkflowPNGExporter,
    private val exporter: WorkflowExporter,
    private val service: WorkflowService,
    private val validator: WorkflowValidator,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ExportWorkflowPNGEndpoint::class.java)
    }

    @GetMapping("/v1/workflows/images/{tenant-id}.{workflow-id}.png")
    fun png(
        @PathVariable("tenant-id") tenantId: Long,
        @PathVariable("workflow-id") id: Long,
        response: HttpServletResponse
    ) {
        response.contentType = "image/png"
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename("$tenantId.$id.png").build().toString()
        )

        try {
            val workflow = service.get(id, tenantId)
            val data = exporter.export(workflow)
            pngExporter.export(data, response.outputStream)
        } catch (ex: NotFoundException) {
            LOGGER.warn("workflow not found", ex)
            response.status = 404
        }
    }

    @PostMapping("/v1/workflows/images")
    fun png(
        @RequestBody @Valid request: ImportWorkflowRequest,
        response: HttpServletResponse
    ) {
        validate(request)

        response.contentType = "image/png"
        pngExporter.export(request.workflow, response.outputStream)
    }

    private fun validate(request: ImportWorkflowRequest) {
        val errors = validator.validate(request.workflow)
        if (errors.isNotEmpty()) {
            var i = 0
            val data = errors.map { error ->
                "%04d".format(i++) to "${error.location} - ${error.message}"
            }.toMap()
            throw BadRequestException(
                error = Error(
                    code = ErrorCode.WORKFLOW_NOT_VALID,
                    data = data
                )
            )
        }
    }
}
