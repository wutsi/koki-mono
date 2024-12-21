package com.wutsi.koki.portal.page.workflow.widget

import com.wutsi.koki.portal.service.CurrentUserHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MyCasesWidgetController(
    private val currentUserHolder: CurrentUserHolder,
    private val delegate: WorkflowInstanceWidgetController,
) {
    @GetMapping("/workflows/widgets/my-cases")
    fun myCases(model: Model): String {
        val me = currentUserHolder.get()
        if (me != null) {
            return delegate.show(
                createById = me.id,
                title = "My Workflow Instances",
                model = model,
            )
        }
        return "workflows/widgets/workflow-instances"
    }
}
