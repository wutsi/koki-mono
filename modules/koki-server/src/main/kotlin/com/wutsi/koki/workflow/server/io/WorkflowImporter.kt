package com.wutsi.koki.workflow.server.io

import com.wutsi.koki.error.exception.NotFoundException
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
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowImporter::class.java)
    }

    fun import(workflow: WorkflowEntity, data: WorkflowData): WorkflowEntity {
        val w = saveWorkflow(workflow, data)
        saveActivities(w, data)
        linkPredecessors(w, data)
        deactivate(w, data)
        return w
    }

    private fun saveActivities(workflow: WorkflowEntity, data: WorkflowData): List<ActivityEntity> {
        return data.activities.map { activity -> saveActivity(workflow, activity) }
    }

    private fun saveActivity(workflow: WorkflowEntity, data: ActivityData): ActivityEntity {
        try {
            val activity = activityService.getByCode(data.code, workflow)

            LOGGER.info(">>> Updating Activity#${data.code}")
            activity.type = data.type
            activity.name = data.name
            activity.description = data.description
            activity.active = true
            activity.requiresApproval = data.requiresApproval
            activity.tags = toString(data.tags)
            return activityService.save(activity)
        } catch (ex: NotFoundException) {
            LOGGER.info(">>> Adding Activity#${data.code}")

            return activityService.save(
                ActivityEntity(
                    workflow = workflow,
                    type = data.type,
                    code = data.code.uppercase(),
                    name = data.name,
                    description = data.description,
                    active = true,
                    requiresApproval = data.requiresApproval,
                    tags = toString(data.tags)
                )
            )
        }
    }

    private fun linkPredecessors(workflow: WorkflowEntity, data: WorkflowData) {
        data.activities.map { activity -> linkPredecessors(workflow, activity) }
    }

    private fun linkPredecessors(workflow: WorkflowEntity, data: ActivityData) {
        val activity = activityService.getByCode(data.code, workflow)
        LOGGER.info(">>> Linking Activity#${activity.code} with ${data.predecessors}")

        activity.predecessors.clear()
        activity.predecessors.addAll(activityService.getByCodes(data.predecessors, workflow))
        activityService.save(activity)
    }

    private fun saveWorkflow(workflow: WorkflowEntity, data: WorkflowData): WorkflowEntity {
        workflow.name = data.name
        workflow.description = data.description
        workflow.active = true
        return workflowService.save(workflow)
    }

    private fun deactivate(workflow: WorkflowEntity, data: WorkflowData) {
        val codes = data.activities.map { activity -> activity.code.uppercase() }
        val activities = activityService.getByWorkflow(workflow)

        activities.forEach { activity ->
            if (!codes.contains(activity.code.uppercase())) {
                LOGGER.info(">>> Deactivating Activity#${activity.code}")

                activity.active = false
                activityService.save(activity)
            }
        }
    }

    private fun toString(tags: Map<String, String>): String {
        return tags.entries.map { entry -> "${entry.key}=${entry.value}" }.joinToString(separator = "\n")
    }
}
