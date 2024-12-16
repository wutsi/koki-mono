package com.wutsi.koki.workflow.server.io

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.server.service.FormService
import com.wutsi.koki.message.server.service.MessageService
import com.wutsi.koki.script.server.service.ScriptService
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.FlowEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.stereotype.Service
import java.io.OutputStream

@Service
class WorkflowExporter(
    private val roleService: RoleService,
    private val formService: FormService,
    private val messageService: MessageService,
    private val scriptService: ScriptService,
    private val objectMapper: ObjectMapper,
) {
    fun export(workflow: WorkflowEntity): WorkflowData {
        return WorkflowData(
            name = workflow.name,
            title = workflow.title,
            description = workflow.description,
            parameters = workflow.parameterAsList(),
            flows = workflow.flows
                .filter { flow -> flow.from.active && flow.to.active }
                .map { flow -> toFlowData(flow) },
            activities = workflow.activities
                .filter { activty -> activty.active }
                .map { activity -> toActivityData(activity, workflow.tenantId) },
            approverRole = workflow.approverRoleId?.let { roleId ->
                roleService.get(roleId, workflow.tenantId)?.name
            },
        )
    }

    fun export(workflow: WorkflowEntity, output: OutputStream) {
        val data = export(workflow)
        objectMapper.writeValue(output, data)
    }

    private fun toFlowData(flow: FlowEntity): FlowData {
        return FlowData(
            from = flow.from.name,
            to = flow.to.name,
            expression = flow.expression,
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun toActivityData(activity: ActivityEntity, tenantId: Long): ActivityData {
        val form = activity.formId?.let { id -> formService.get(id, tenantId) }
        val role = activity.roleId?.let { id -> roleService.get(id, tenantId) }
        val message = activity.messageId?.let { id -> messageService.get(id, tenantId) }
        val script = activity.scriptId?.let { id -> scriptService.get(id, tenantId) }
        return ActivityData(
            name = activity.name,
            type = activity.type,
            title = activity.title,
            description = activity.description,
            requiresApproval = activity.requiresApproval,
            form = form?.name,
            role = role?.name,
            message = message?.name,
            script = script?.name,
            event = activity.event,
            input = activity.inputAsMap(objectMapper),
            output = activity.outputAsMap(objectMapper),
        )
    }
}
