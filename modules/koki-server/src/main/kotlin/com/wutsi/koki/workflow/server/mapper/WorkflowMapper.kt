package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowSummary
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.util.JGraphtUtil
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WorkflowMapper(
    private val activityMapper: ActivityMapper,
    private val flowMapper: FlowMapper,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowMapper::class.java)
    }

    fun toWorkflow(entity: WorkflowEntity): Workflow {
        val activities = entity.activities.map { activity -> activityMapper.toActivity(activity) }
        return Workflow(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            activities = activities,
            roleIds = entity.activities
                .mapNotNull { activity -> activity.roleId }
                .distinctBy { roleId -> roleId }
                .sorted(),
            requiresApprover = sortActivities(entity).find { activity -> activity.requiresApproval } != null,
            parameters = entity.parameterAsList(),
            flows = entity.flows.map { flow -> flowMapper.toFlow(flow) },
            approverRoleId = entity.approverRoleId,
            workflowInstanceCount = entity.workflowInstanceCount,
        )
    }

    fun toWorkflowSummary(entity: WorkflowEntity): WorkflowSummary {
        return WorkflowSummary(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    private fun sortActivities(workflow: WorkflowEntity): List<ActivityEntity> {
        try {
            val graph = JGraphtUtil.createGraph(workflow)
            val iterator = TopologicalOrderIterator<String, DefaultEdge>(graph)
            val activityMap = workflow.activities.associateBy { activity -> activity.name }
            val result = mutableListOf<ActivityEntity>()
            while (iterator.hasNext()) {
                val next = iterator.next()
                result.add(activityMap[next]!!)
            }
            return result
        } catch (ex: Exception) {
            LOGGER.warn("Unable to perform topological story on workflow#${workflow.id}", ex)
            return workflow.activities
        }
    }
}
