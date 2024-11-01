package com.wutsi.koki.workflow.server.io

import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.dto.JSONActivity
import com.wutsi.koki.workflow.dto.JSONWorkflow
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.WorkflowService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WorkflowJSONImporter(
    private val workflowService: WorkflowService,
    private val activityService: ActivityService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowJSONImporter::class.java)
    }

    fun import(workflow: WorkflowEntity, json: JSONWorkflow): WorkflowEntity {
        val w = saveWorkflow(workflow, json)
        saveActivities(workflow, json)
        linkPrecedents(workflow, json)
        deactivate(workflow, json)
        return w
    }

    private fun saveActivities(workflow: WorkflowEntity, json: JSONWorkflow): List<ActivityEntity> {
        return json.activities.map { activity -> saveActivity(workflow, activity) }
    }

    private fun saveActivity(workflow: WorkflowEntity, json: JSONActivity): ActivityEntity {
        try {
            LOGGER.info(">>> Updating Activity#${json.code}")

            val activity = activityService.getByCode(json.code, workflow)
            activity.type = json.type
            activity.name = json.name
            activity.description = json.description
            activity.active = true
            activity.requiresApproval = json.requiresApproval
            activity.tags = toString(json.tags)
            return activityService.save(activity)
        } catch (ex: NotFoundException) {
            LOGGER.info(">>> Adding Activity#${json.code}")

            return activityService.save(
                ActivityEntity(
                    type = json.type,
                    code = json.code.lowercase(),
                    name = json.name,
                    description = json.description,
                    active = true,
                    requiresApproval = json.requiresApproval,
                    tags = toString(json.tags)
                )
            )
        }
    }

    private fun linkPrecedents(workflow: WorkflowEntity, json: JSONWorkflow) {
        json.activities.map { activity -> linkPrecedent(workflow, activity) }
    }

    private fun linkPrecedent(workflow: WorkflowEntity, json: JSONActivity) {
        val activity = activityService.getByCode(json.code, workflow)
        LOGGER.info(">>> Linking Activity#${activity.code} with ${json.precedents}")

        activity.precedents.clear()
        activity.precedents.addAll(activityService.getByCodes(json.precedents, workflow))
    }

    private fun saveWorkflow(workflow: WorkflowEntity, json: JSONWorkflow): WorkflowEntity {
        workflow.name = json.name
        workflow.description = json.description
        workflowService.save(workflow)
    }

    private fun deactivate(workflow: WorkflowEntity, json: JSONWorkflow) {
        val codes = json.activities.map { activity -> activity.code.lowercase() }
        val activities = activityService.getByWorkflow(workflow)

        activities.forEach { activity ->
            if (!codes.contains(activity.code.lowercase())) {
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
