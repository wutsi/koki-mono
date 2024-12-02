package com.wutsi.koki.portal.page.settings.workflow

import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.service.WorkflowService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.client.HttpClientErrorException
import kotlin.collections.map
import kotlin.collections.sorted

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
