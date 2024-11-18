package com.wutsi.koki.portal.page.workflow

import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.rest.WorkflowService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.client.HttpClientErrorException

@Controller
abstract class AbstractSaveWorkflowController(
    protected val service: WorkflowService,
) : AbstractPageController() {
    protected fun loadErrors(ex: HttpClientErrorException, model: Model) {
        val response = toErrorResponse(ex)
        model.addAttribute("error", response.error.code)
        model.addAttribute("errorParam", response.error.parameter?.value)
        model.addAttribute(
            "errorDetails",
            response.error
                .data
                ?.map { entry ->
                    "${entry.key} - ${entry.value}"
                }?.sorted()
        )
    }
}
