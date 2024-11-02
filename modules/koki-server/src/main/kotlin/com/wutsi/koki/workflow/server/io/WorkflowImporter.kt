package com.wutsi.koki.workflow.server.io

import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.domain.RoleEntity
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.WorkflowService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WorkflowImporter(
    private val workflowService: WorkflowService,
    private val activityService: ActivityService,
    private val roleService: RoleService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowImporter::class.java)
    }

    fun import(workflow: WorkflowEntity, data: WorkflowData): WorkflowEntity {
        val w = saveWorkflow(workflow, data)

        // Update activities
        val activities = saveActivities(w, data)
        linkPredecessors(w, activities, data)
        linkRoles(w, activities, data)
        activityService.saveAll(activities)

        // Deactivate activities
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

            LOGGER.info(">>> Updating Activity#${data.name}")
            activity.type = data.type
            activity.description = data.description
            activity.active = true
            activity.requiresApproval = data.requiresApproval
            activity.tags = toString(data.tags)
            return activity
        } catch (ex: NotFoundException) {
            LOGGER.info(">>> Adding Activity#${data.name}")

            return activityService.save(
                ActivityEntity(
                    workflow = workflow,
                    type = data.type,
                    name = data.name,
                    description = data.description,
                    active = true,
                    requiresApproval = data.requiresApproval,
                    tags = toString(data.tags)
                )
            )
        }
    }

    private fun linkPredecessors(workflow: WorkflowEntity, activities: List<ActivityEntity>, data: WorkflowData) {
        val predecessorNames = data.activities.flatMap { activity -> activity.predecessors }
        val predecessorMap = if (predecessorNames.isEmpty()) {
            emptyMap()
        } else {
            activityService.getByNames(predecessorNames, workflow)
                .associateBy { activity -> activity.name }
        }
        activities.map { activity -> linkPredecessors(activity, predecessorMap, data) }
    }

    private fun linkPredecessors(
        activity: ActivityEntity,
        predecessorMap: Map<String, ActivityEntity>,
        data: WorkflowData
    ) {
        val predecessorNames: List<String> = data.activities
            .find { act -> act.name.equals(activity.name, true) }
            ?.predecessors
            ?: emptyList()
        LOGGER.info(">>> Linking Activity[${activity.name}] with Predecessors$predecessorNames")

        val predecessors = predecessorNames.mapNotNull { name -> predecessorMap[name] }

        activity.predecessors.clear()
        activity.predecessors.addAll(predecessors)
    }

    private fun linkRoles(workflow: WorkflowEntity, activities: List<ActivityEntity>, data: WorkflowData) {
        val roleNames = data.activities.mapNotNull { activity -> activity.role?.ifEmpty { null } }
        val roleMap = if (roleNames.isEmpty()) {
            emptyMap()
        } else {
            roleService.search(roleNames, workflow.tenant.id ?: -1)
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
        LOGGER.info(">>> Linking Activity[${activity.name}] with Role[$role]")

        activity.role = role?.let { roleMap[role] }
    }

    private fun saveWorkflow(workflow: WorkflowEntity, data: WorkflowData): WorkflowEntity {
        workflow.name = data.name
        workflow.description = data.description
        workflow.active = true
        return workflowService.save(workflow)
    }

    private fun deactivate(workflow: WorkflowEntity, data: WorkflowData): List<ActivityEntity> {
        val codes = data.activities.map { activity -> activity.name }
        val activities = activityService.getByWorkflow(workflow)

        val result = mutableListOf<ActivityEntity>()
        activities.forEach { activity ->
            if (!codes.contains(activity.name)) {
                LOGGER.info(">>> Deactivating Activity#${activity.name}")

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
