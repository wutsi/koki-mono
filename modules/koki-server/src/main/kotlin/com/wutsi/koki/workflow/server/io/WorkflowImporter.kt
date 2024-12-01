package com.wutsi.koki.workflow.server.io

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.server.service.FormService
import com.wutsi.koki.message.server.service.MessageService
import com.wutsi.koki.tenant.server.domain.RoleEntity
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.FlowEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.FlowService
import com.wutsi.koki.workflow.server.service.WorkflowService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WorkflowImporter(
    private val workflowService: WorkflowService,
    private val activityService: ActivityService,
    private val flowService: FlowService,
    private val roleService: RoleService,
    private val formService: FormService,
    private val messageService: MessageService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowImporter::class.java)
    }

    fun import(workflow: WorkflowEntity, data: WorkflowData): WorkflowEntity {
        ensureHasNoInstance(workflow)
        ensureNameUnique(workflow, data)

        val w = saveWorkflow(workflow, data)

        // Update activities
        val activities = saveActivities(w, data)
        addFlows(w, activities, data)
        linkRoles(w, activities, data)
        linkForms(w, activities, data)
        linkMessages(w, activities, data)
        activityService.saveAll(activities)

        // Deactivate old activities
        val xactivities = deactivate(w, data)
        activityService.saveAll(xactivities)

        return w
    }

    private fun saveActivities(workflow: WorkflowEntity, data: WorkflowData): List<ActivityEntity> {
        return data.activities.map { activity -> saveActivity(workflow, activity) }
    }

    private fun saveActivity(workflow: WorkflowEntity, data: ActivityData): ActivityEntity {
        try {
            val activity = activityService.getByName(data.name, workflow)

            if (LOGGER.isDebugEnabled) {
                LOGGER.debug(">>> Updating Activity#${data.name}")
            }
            activity.type = data.type
            activity.title = data.title
            activity.description = data.description
            activity.active = true
            activity.requiresApproval = data.requiresApproval
            activity.tags = toString(data.tags)
            return activity
        } catch (ex: NotFoundException) {
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug(">>> Adding Activity#${data.name}")
            }
            return activityService.save(
                ActivityEntity(
                    workflowId = workflow.id!!,
                    tenantId = workflow.tenantId,
                    type = data.type,
                    title = data.title,
                    name = data.name,
                    description = data.description,
                    active = true,
                    requiresApproval = data.requiresApproval,
                    tags = toString(data.tags),
                )
            )
        }
    }

    private fun addFlows(
        workflow: WorkflowEntity,
        activities: List<ActivityEntity>,
        data: WorkflowData
    ): List<FlowEntity> {
        // Add/Update
        val flows = data.flows.mapNotNull { flow -> addFlow(workflow, activities, flow) }

        // Delete un old flows
        val flowIds = flows.mapNotNull { flow -> flow.id }
        val flowToDelete = workflow.flows.filter { flow ->
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug(">>> Deleting Activity[${flow.from.name}] -> Activity[${flow.to.name}]")
            }
            !flowIds.contains(flow.id)
        }
        flowService.deleteAll(flowToDelete)

        return flows
    }

    private fun addFlow(workflow: WorkflowEntity, activities: List<ActivityEntity>, flow: FlowData): FlowEntity? {
        val from = activities.find { activity -> activity.name == flow.from } ?: return null
        val to = activities.find { activity -> activity.name == flow.to } ?: return null

        try {
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug(">>> Updating Activity[${flow.from}] -> Activity[${flow.to}]")
            }
            val entity = flowService.get(from, to)
            entity.expression = flow.expression
            flowService.save(entity)
            return entity
        } catch (ex: NotFoundException) {
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug(">>> Adding Activity[${flow.from}] -> Activity[${flow.to}]")
            }
            return flowService.save(
                FlowEntity(
                    workflowId = workflow.id!!,
                    from = from,
                    to = to,
                    expression = flow.expression
                )
            )
        }
    }

    private fun linkRoles(workflow: WorkflowEntity, activities: List<ActivityEntity>, data: WorkflowData) {
        val roleNames = data.activities.mapNotNull { activity -> activity.role?.ifEmpty { null } }
        val roleMap = if (roleNames.isEmpty()) {
            emptyMap()
        } else {
            roleService.search(names = roleNames, tenantId = workflow.tenantId)
                .associateBy { role -> role.name }
        }

        activities.map { activity -> linkRoles(activity, roleMap, data) }
    }

    private fun linkRoles(
        activity: ActivityEntity,
        roleMap: Map<String, RoleEntity>,
        data: WorkflowData
    ) {
        val role: String? = data.activities
            .find { act -> act.name.equals(activity.name, true) }
            ?.role
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug(">>> Linking Activity[${activity.name}] with Role[$role]")
        }
        activity.roleId = role?.let { roleMap[role]?.id }
    }

    private fun linkForms(workflow: WorkflowEntity, activities: List<ActivityEntity>, data: WorkflowData) {
        activities.map { activity -> linkForm(workflow, activity, data) }
    }

    private fun linkForm(workflow: WorkflowEntity, activity: ActivityEntity, data: WorkflowData) {
        val activityData = data.activities.find { act -> act.name == activity.name }
        if (activityData?.form != null) {
            LOGGER.debug(">>> Linking Activity[${activity.name}] with Form[${activityData.form}]")
            activity.formId = formService.getByName(activityData.form!!, workflow.tenantId).id
        } else {
            activity.formId = null
        }
    }

    private fun linkMessages(workflow: WorkflowEntity, activities: List<ActivityEntity>, data: WorkflowData) {
        activities.map { activity -> linkMessage(workflow, activity, data) }
    }

    private fun linkMessage(workflow: WorkflowEntity, activity: ActivityEntity, data: WorkflowData) {
        val activityData = data.activities.find { act -> act.name == activity.name }
        if (activityData?.message != null) {
            LOGGER.debug(">>> Linking Activity[${activity.name}] with Messager[${activityData.message}]")
            activity.messageId = messageService.getByName(activityData.message!!, workflow.tenantId).id
        } else {
            activity.messageId = null
        }
    }

    private fun saveWorkflow(workflow: WorkflowEntity, data: WorkflowData): WorkflowEntity {
        workflow.name = data.name
        workflow.title = data.title
        workflow.description = data.description
        workflow.active = true
        workflow.parameters = data.parameters
            .map { param -> param.trim() }
            .joinToString(separator = ",")
            .ifEmpty { null }
        workflow.approverRoleId = data.approverRole?.ifEmpty { null }?.let { roleName ->
            roleService.getByName(roleName, workflow.tenantId).id
        }
        return workflowService.save(workflow)
    }

    private fun ensureHasNoInstance(workflow: WorkflowEntity) {
        if (workflow.workflowInstanceCount > 0) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WORKFLOW_HAS_INSTANCES,
                )
            )
        }
    }

    private fun ensureNameUnique(workflow: WorkflowEntity, data: WorkflowData) {
        try {
            val duplicate = workflowService.get(data.name, workflow.tenantId)
            if (duplicate.id != workflow.id) {
                throw ConflictException(
                    error = Error(
                        code = ErrorCode.WORKFLOW_DUPLICATE_NAME,
                        parameter = Parameter(value = data.name)
                    )
                )
            }
        } catch (ex: NotFoundException) {
            // Ignore
        }
    }

    private fun deactivate(workflow: WorkflowEntity, data: WorkflowData): List<ActivityEntity> {
        val codes = data.activities.map { activity -> activity.name }
        val activities = activityService.getByWorkflow(workflow)

        val result = mutableListOf<ActivityEntity>()
        activities.forEach { activity ->
            if (!codes.contains(activity.name)) {
                if (LOGGER.isDebugEnabled) {
                    LOGGER.debug(">>> Deactivating Activity#${activity.name}")
                }
                activity.active = false
                result.add(activity)
            }
        }
        return result
    }

    private fun toString(tags: Map<String, String>): String {
        return tags.entries.map { entry -> "${entry.key}=${entry.value}" }.joinToString(separator = "\n")
    }
}
